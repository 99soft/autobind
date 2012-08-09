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
import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static org.nnsoft.guice.autobind.install.BindingStage.BINDING;
import static org.nnsoft.guice.autobind.install.BindingStage.IGNORE;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.inject.Singleton;

import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.install.BindingStage;
import org.nnsoft.guice.autobind.install.bindjob.BindingJob;
import org.nnsoft.guice.autobind.install.bindjob.MultiBindingJob;

import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

@Singleton
public class MultiBindingFeature
    extends AutoBindingFeature
{

    @Override
    public BindingStage accept( Class<Object> annotatedClass, Map<String, Annotation> annotations )
    {
        if ( annotations.containsKey( Bind.class.getName() ) )
        {
            Bind annotation = (Bind) annotations.get( Bind.class.getName() );
            if ( annotation.multiple() )
            {
                return BINDING;
            }
        }
        return IGNORE;
    }

    @Override
    protected <T, V extends T> void bind( Class<V> implementationClass, Class<T> interf, Annotation annotation,
                                          Class<? extends Annotation> scope )
    {
        BindingJob job = new MultiBindingJob( scope, annotation, implementationClass.getName(), interf.getName() );

        if ( !tracer.contains( job ) )
        {
            Multibinder<T> builder;
            synchronized ( _binder )
            {
                if ( annotation != null )
                {
                    builder = newSetBinder( _binder, interf, annotation );
                }
                else
                {
                    builder = newSetBinder( _binder, interf );
                }

                ScopedBindingBuilder scopedBindingBuilder = builder.addBinding().to( implementationClass );
                if ( scope != null )
                {
                    scopedBindingBuilder.in( scope );
                }
            }
            tracer.add( job );
        }
        else
        {
            String message = format( "Ignoring Multi-BindingJob \"%s\", because it was already bound.", job );

            if ( _logger.isLoggable( FINE ) )
            {
                _logger.log( FINE, message, new Exception( message ) );
            }
            else if ( _logger.isLoggable( INFO ) )
            {
                _logger.log( INFO, message );
            }
        }
    }

}
