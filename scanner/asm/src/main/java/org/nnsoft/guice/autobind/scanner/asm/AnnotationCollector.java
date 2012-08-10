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

import static java.util.Collections.unmodifiableList;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;
import static org.objectweb.asm.ClassReader.SKIP_CODE;
import static org.objectweb.asm.ClassReader.SKIP_DEBUG;
import static org.objectweb.asm.ClassReader.SKIP_FRAMES;
import static org.objectweb.asm.Opcodes.ASM4;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.nnsoft.guice.autobind.scanner.features.ScannerFeature;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

/**
 * Visitor implementation to collect field annotation information from class.
 */
final class AnnotationCollector
    extends ClassVisitor
{

    public static final int ASM_FLAGS = SKIP_CODE | SKIP_DEBUG | SKIP_FRAMES;

    private final Logger _logger = getLogger( AnnotationCollector.class.getName() );

    private String _name;

    private Class<?> _class;

    private boolean _ignore;

    private final Map<String, Annotation> _annotations;

    private final List<ScannerFeature> _features;

    public AnnotationCollector()
    {
        super( ASM4 );
        _features = new LinkedList<ScannerFeature>();
        _annotations = new HashMap<String, Annotation>();
    }

    public void addScannerFeature( ScannerFeature listener )
    {
        _features.add( listener );
    }

    public void removerScannerFeature( ScannerFeature listener )
    {
        _features.remove( listener );
    }

    public List<ScannerFeature> getScannerFeatures()
    {
        return unmodifiableList( _features );
    }

    public void destroy()
    {
        _annotations.clear();
        _class = null;
        _features.clear();
    }

    @Override
    public void visit( int version, int access, String name, String signature, String superName, String[] interfaces )
    {
        _name = name.replace( '/', '.' );
        for ( String interf : interfaces )
        {
            if ( interf.equals( "java/lang/annotation/Annotation" ) )
            {
                _ignore = true;
                return;
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public AnnotationVisitor visitAnnotation( String sig, boolean visible )
    {
        if ( _ignore )
        {
            return null;
        }
        String annotationClassStr = sig.replace( '/', '.' ).substring( 1, sig.length() - 1 );
        if ( _class == null )
        {
            try
            {
                _class = Thread.currentThread().getContextClassLoader().loadClass( _name );
            }
            catch ( ClassNotFoundException e )
            {
                _logger.log( WARNING, "Failure while visitAnnotation. Class could not be loaded.", e );
                return null;
            }
        }
        try
        {
            Class<Annotation> annotationClass =
                (Class<Annotation>) getClass().getClassLoader().loadClass( annotationClassStr );
            Annotation annotation = _class.getAnnotation( annotationClass );
            _annotations.put( annotationClassStr, annotation );
        }
        catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
            _logger.log( WARNING, "Failure while visitAnnotation. Class could not be loaded.", e );
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void visitEnd()
    {
        if ( !_ignore && _annotations.size() > 0 && !_annotations.containsKey( "javax.enterprise.inject.Alternative" ) )
        {
            for ( ScannerFeature listener : _features )
            {
                listener.found( (Class<Object>) _class, _annotations );
            }
        }
        _name = null;
        _class = null;
        _ignore = false;
        _annotations.clear();
    }

}
