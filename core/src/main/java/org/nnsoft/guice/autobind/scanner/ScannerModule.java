package org.nnsoft.guice.autobind.scanner;

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

import static java.lang.System.getProperty;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.annotations.GuiceModule;
import org.nnsoft.guice.autobind.install.InstallationContext;
import org.nnsoft.guice.autobind.scanner.features.BindingScannerFeature;
import org.nnsoft.guice.autobind.scanner.features.ScannerFeature;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * The ScannerModule will be injected with a ClasspathScanner and the needed
 * Annotation Listeners will be added. The attached Listeners will install all
 * Modules annotated with {@link GuiceModule} and bind all Beans annotated with
 * {@link Bind}.
 */
public class ScannerModule
    implements Module
{

    public static String LINE_SEPARATOR = getProperty( "line.separator" );

    private final Logger _logger = getLogger( getClass().getName() );

    @Inject
    private ClasspathScanner _scanner;

    @Inject
    private Set<ScannerFeature> _listeners;

    @Inject
    private InstallationContext _context;

    @Override
    public void configure( Binder binder )
    {
        if ( _logger.isLoggable( INFO ) )
        {
            StringBuilder builder = new StringBuilder( _scanner.getClass().getName() )
                                    .append( " is using following Scanner Features: " )
                                    .append( LINE_SEPARATOR );
            for ( ScannerFeature listener : _listeners )
            {
                builder.append( listener.getClass().getName() ).append( LINE_SEPARATOR );
            }
            _logger.log( INFO, builder.toString() );
        }
        for ( ScannerFeature listener : _listeners )
        {
            if ( listener instanceof BindingScannerFeature )
            {
                ( (BindingScannerFeature) listener ).setBinder( binder );
                if ( _logger.isLoggable( FINE ) )
                {
                    _logger.fine( "Binding AnnotationListeners " + listener.getClass().getName() );
                }
            }
        }
        try
        {
            _scanner.scan();
        }
        catch ( IOException e )
        {
            _logger.log( SEVERE, "Failure while Scanning the Classpath for Classes with Annotations.", e );
        }
        try
        {
            _context.process();
        }
        catch ( Exception e )
        {
            _logger.log( SEVERE, "Failure while executing the collected Tasks.", e );
        }
    }

}
