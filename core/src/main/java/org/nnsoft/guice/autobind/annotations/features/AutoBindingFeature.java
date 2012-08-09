package org.nnsoft.guice.autobind.annotations.features;

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

import static java.lang.String.format;
import static java.util.Collections.addAll;
import static java.util.logging.Logger.getLogger;
import static org.nnsoft.guice.autobind.annotations.To.Type.IMPLEMENTATION;
import static org.nnsoft.guice.autobind.install.BindingStage.BINDING;
import static org.nnsoft.guice.autobind.install.BindingStage.IGNORE;
import static org.nnsoft.guice.autobind.jsr330.Names.named;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.annotations.GuiceAnnotation;
import org.nnsoft.guice.autobind.install.BindingStage;
import org.nnsoft.guice.autobind.scanner.features.BindingScannerFeature;

import com.google.inject.BindingAnnotation;

@Singleton
public class AutoBindingFeature
    extends BindingScannerFeature
{

    protected final Logger _logger = getLogger( getClass().getName() );

    @Override
    public BindingStage accept( Class<Object> annotatedClass, Map<String, Annotation> annotations )
    {
        if ( annotations.containsKey( Bind.class.getName() ) )
        {
            Bind annotation = (Bind) annotations.get( Bind.class.getName() );
            if ( !annotation.multiple() && !( IMPLEMENTATION == annotation.to().value() ) )
            {
                return BINDING;
            }
        }
        return IGNORE;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public void process( final Class<Object> annotatedClass, final Map<String, Annotation> annotations )
    {
        Bind annotation = (Bind) annotations.get( Bind.class.getName() );
        Map<String, Annotation> filtered = filter( annotations );

        final boolean asSingleton =
            ( annotations.containsKey( com.google.inject.Singleton.class.getName() ) || annotations.containsKey( javax.inject.Singleton.class.getName() ) );

        if ( annotation.value().value().length() > 0 )
        {
            filtered.put( Named.class.getName(), named( resolver.resolve( annotation.value().value() ) ) );
        }

        Class<Object>[] interfaces;

        switch ( annotation.to().value() )
        {
            case CUSTOM:
                interfaces = (Class<Object>[]) annotation.to().customs();
                break;

            case SUPER:
                Class<? super Object> superclass = annotatedClass.getSuperclass();
                if ( Object.class.equals( superclass ) )
                {
                    interfaces = new Class[] { annotatedClass };
                }
                else
                {
                    interfaces = new Class[] { superclass };
                }

                break;

            case INTERFACES:
            default:
                interfaces = (Class<Object>[]) annotatedClass.getInterfaces();
                if ( interfaces.length == 0 )
                {
                    List<Class<?>> interfaceCollection = new ArrayList<Class<?>>();
                    Class<? super Object> parent = annotatedClass.getSuperclass();
                    while ( parent != null && !parent.equals( Object.class ) )
                    {
                        addAll( interfaceCollection, parent.getInterfaces() );
                        parent = parent.getSuperclass();
                    }
                    interfaces = interfaceCollection.toArray( new Class[interfaceCollection.size()] );
                    if ( interfaces.length == 0 )
                    {
                        interfaces = new Class[] { annotatedClass };
                        // FIXME Guice doesn't allow a binding to itself
                    }
                }
        }

        for ( Class<Object> interf : interfaces )
        {
            if ( _logger.isLoggable( Level.FINE ) )
            {
                _logger.fine( format( "Binding Class %s to Interface %s. Singleton? %s ",
                                      annotatedClass, interf, asSingleton ) );
            }

            if ( filtered.size() > 0 )
            {
                for ( Annotation anno : filtered.values() )
                {
                    bind( annotatedClass, interf, anno, ( asSingleton ? Singleton.class : null ) );
                }
            }
            else
            {
                bind( annotatedClass, interf, null, ( asSingleton ? Singleton.class : null ) );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    protected Map<String, Annotation> filter( final Map<String, Annotation> annotations )
    {
        Map<String, Annotation> filtered = new HashMap<String, Annotation>( annotations );

        for ( Entry<String, Annotation> entry : annotations.entrySet() )
        {
            String key = entry.getKey();
            if ( qualifiers.contains( key ) )
            {
                continue;
            }
            if ( others.contains( key ) )
            {
                filtered.remove( key );
                continue;
            }
            Class<? extends Annotation> annotation;
            try
            {
                annotation = (Class<? extends Annotation>) Class.forName( key );
                if ( annotation.isAnnotationPresent( GuiceAnnotation.class ) )
                {
                    filtered.remove( key );
                    others.add( key );
                    continue;
                }
                if ( annotation.isAnnotationPresent( Qualifier.class ) )
                {
                    qualifiers.add( key );
                    continue;
                }
                if ( annotation.isAnnotationPresent( BindingAnnotation.class ) )
                {
                    qualifiers.add( key );
                    continue;
                }
                filtered.remove( key );
                others.add( key );
            }
            catch ( ClassNotFoundException e )
            {
                // TODO ignore
            }
        }

        return filtered;
    }

}
