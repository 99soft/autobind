package org.nnsoft.guice.autobind.scanner.asm;

/*
 *    Copyright 2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.Collections.synchronizedSet;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;
import static org.nnsoft.guice.autobind.scanner.asm.AnnotationCollector.ASM_FLAGS;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.nnsoft.guice.autobind.scanner.ClasspathScanner;
import org.nnsoft.guice.autobind.scanner.PackageFilter;
import org.nnsoft.guice.autobind.scanner.features.ScannerFeature;
import org.objectweb.asm.ClassReader;

/**
 * This Implementation only uses the ASM-API to read all recognized classes. It
 * doesn't depend on any further 3rd-Party libraries.
 */
public class ASMClasspathScanner
    implements ClasspathScanner
{

    private static final String LINE_SEPARATOR = getProperty( "line.separator" );

    private final Logger _logger = getLogger( getClass().getName() );

    @Inject
    @Named( "classpath" )
    private URL[] classPath;

    private List<Pattern> patterns = new ArrayList<Pattern>();

    private Set<String> visited;

    private final BlockingQueue<AnnotationCollector> collectors;

    @Inject
    public ASMClasspathScanner( Set<ScannerFeature> listeners, @Named( "packages" ) PackageFilter... filter )
    {
        int cores = getRuntime().availableProcessors();
        this.collectors = new ArrayBlockingQueue<AnnotationCollector>( cores );

        for ( int i = 0; i < cores; i++ )
        {
            try
            {
                collectors.put( new AnnotationCollector() );
            }
            catch ( InterruptedException e )
            {
                // ignore
            }
        }
        for ( PackageFilter p : filter )
        {
            includePackage( p );
        }

        for ( ScannerFeature listener : listeners )
        {
            addFeature( listener );
        }
        visited = synchronizedSet( new HashSet<String>() );
    }

    @Override
    public void addFeature( ScannerFeature feature )
    {
        for ( AnnotationCollector collector : collectors )
        {
            collector.addScannerFeature( feature );
        }
    }

    @Override
    public void removeFeature( ScannerFeature feature )
    {
        for ( AnnotationCollector collector : collectors )
        {
            collector.addScannerFeature( feature );
        }
    }

    @Override
    public List<ScannerFeature> getFeatures()
    {
        List<ScannerFeature> features;
        try
        {
            AnnotationCollector collector = collectors.take();
            features = collector.getScannerFeatures();
            collectors.put( collector );
        }
        catch ( InterruptedException e )
        {
            // ignore
            features = Collections.emptyList();
        }
        return features;
    }

    @Override
    public void includePackage( final PackageFilter filter )
    {
        String packageName = filter.getPackage();
        String pattern = ".*" + packageName.replace( ".", "/" );

        if ( filter.deep() )
        {
            pattern = pattern + "/(?:\\w|/)*([A-Z](?:\\w|\\$)+)\\.class$";
        }
        else
        {
            pattern = pattern + "/([A-Z](?:\\w|\\$)+)\\.class$";
        }

        if ( _logger.isLoggable( FINE ) )
        {
            _logger.fine( format( "Including Package for scanning: %s generating Pattern: %s", packageName,  pattern ) );
        }
        patterns.add( Pattern.compile( pattern ) );
    }

    @Override
    public void excludePackage( final PackageFilter filter )
    {
        // TODO Could use Predicate of Google
    }

    public void scan()
        throws IOException
    {
        ExecutorService pool = newFixedThreadPool( getRuntime().availableProcessors() );

        if ( _logger.isLoggable( INFO ) )
        {
            StringBuilder builder = new StringBuilder();
            builder.append( "Using Root-Path for Classpath scanning:" ).append( LINE_SEPARATOR );
            for ( URL url : classPath )
            {
                builder.append( url.toString() ).append( LINE_SEPARATOR );
            }
            _logger.log( INFO, builder.toString() );
        }

        List<Future<?>> futures = new ArrayList<Future<?>>();
        for ( final URL url : classPath )
        {
            Future<?> task = pool.submit( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if ( url.toString().startsWith( "jar:" ) )
                        {
                            visitJar( url );
                            return;
                        }
                        URI uri;
                        File entry;
                        try
                        {
                            uri = url.toURI();
                            entry = new File( uri );
                            if ( !entry.exists() )
                            {
                                _logger.log( FINE, format( "Skipping Entry %s, because it doesn't exists.", entry ) );
                                return;
                            }
                        }
                        catch ( URISyntaxException e )
                        {
                            // ignore
                            _logger.log( WARNING, format( "Using invalid URL for Classpath Scanning: %s", url ), e );
                            return;
                        }
                        catch ( Throwable e )
                        {
                            // ignore
                            _logger.log( SEVERE, format( "Using invalid URL for Classpath Scanning: ", url ), e );
                            return;
                        }

                        if ( entry.isDirectory() )
                        {
                            visitFolder( entry );
                        }
                        else
                        {
                            String path = uri.toString();
                            if ( matches( path ) )
                            {
                                if ( !visited.contains( entry.getAbsolutePath() ) )
                                {
                                    visitClass( new FileInputStream( entry ) );
                                    visited.add( entry.getAbsolutePath() );
                                }
                            }
                            else if ( path.endsWith( ".jar" ) )
                            {
                                visitJar( entry );
                            }
                        }
                    }
                    catch ( FileNotFoundException e )
                    {
                        _logger.log( FINE, format( "Skipping Entry %s, because it doesn't exists.", url ), e );
                    }
                    catch ( IOException e )
                    {
                        _logger.log( FINE, format( "Skipping Entry %s, because it couldn't be scanned.", url ),  e );
                    }
                    catch ( Throwable e )
                    {
                        _logger.log( WARNING, format( "Skipping Entry %s, because it couldn't be scanned.", url ),  e );
                    }
                }
            } );
            futures.add( task );
        }
        for ( Future<?> future : futures )
        {
            try
            {
                future.get();
            }
            catch ( InterruptedException e )
            {
                throw new RuntimeException( e );
            }
            catch ( ExecutionException e )
            {
                _logger.log( SEVERE, e.getMessage(), e );
            }
        }
        pool.shutdown();
        destroy();
    }

    public void destroy()
    {
        classPath = null;
        collectors.clear();
        patterns.clear();
        patterns = null;
        visited.clear();
        visited = null;
    }

    private void visitFolder( File folder )
        throws IOException
    {
        _logger.log( FINE, format( "Scanning Folder: %s...", folder.getAbsolutePath() ) );

        File[] files = folder.listFiles();
        for ( File file : files )
        {
            if ( file.isDirectory() )
            {
                visitFolder( file );
            }
            else
            {
                String path = file.toURI().toString();
                if ( matches( path ) )
                {
                    if ( !visited.contains( file.getAbsolutePath() ) )
                    {
                        visitClass( new FileInputStream( file ) );
                        visited.add( file.getAbsolutePath() );
                    }
                }
                else if ( path.endsWith( ".jar" ) )
                {
                    visitJar( file );
                }
            }
        }
    }

    private void visitJar( URL url )
        throws IOException
    {
        if ( _logger.isLoggable( FINE ) )
        {
            _logger.log( FINE, format( "Scanning JAR-File: %s", url ) );
        }

        JarURLConnection conn = (JarURLConnection) url.openConnection();
        _visitJar( conn.getJarFile() );
    }

    private void visitJar( File file )
        throws IOException
    {
        if ( _logger.isLoggable( FINE ) )
        {
            _logger.log( FINE, format( "Scanning JAR-File: %s", file.getAbsolutePath() ) );
        }

        JarFile jarFile = new JarFile( file );
        _visitJar( jarFile );
    }

    private void _visitJar( JarFile jarFile )
        throws IOException
    {
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        for ( JarEntry jarEntry = null; jarEntries.hasMoreElements(); )
        {
            jarEntry = jarEntries.nextElement();
            String name = jarEntry.getName();

            if ( !jarEntry.isDirectory() && matches( name ) )
            {
                if ( !visited.contains( name ) )
                {
                    visitClass( jarFile.getInputStream( jarEntry ) );
                    visited.add( name );
                }
            }
        }
    }

    private void visitClass( InputStream in )
        throws IOException
    {
        ClassReader reader = new ClassReader( new BufferedInputStream( in ) );
        try
        {
            AnnotationCollector collector = collectors.take();
            reader.accept( collector, ASM_FLAGS );
            collectors.put( collector );
        }
        catch ( InterruptedException e )
        {
            // ignore
        }
    }

    private boolean matches( String name )
    {
        boolean returned = false;
        try
        {
            for ( Pattern pattern : patterns )
            {
                if ( pattern.matcher( name ).matches() )
                {
                    return ( returned = true );
                }
            }
            return returned;
        }
        finally
        {
            if ( _logger.isLoggable( Level.FINE ) )
            {
                _logger.log( FINE, format( "%s.matches(..) - \"%s\" -> %s",
                                           getClass().getSimpleName(), name, returned ) );
            }
        }
    }

}
