package org.nnsoft.guice.autobind.scanner.features;

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
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Provider;

import org.nnsoft.guice.autobind.install.BindingStage;
import org.nnsoft.guice.autobind.install.BindingTracer;
import org.nnsoft.guice.autobind.install.InstallationContext;
import org.nnsoft.guice.autobind.install.InstallationContext.StageableRequest;
import org.nnsoft.guice.autobind.install.bindjob.BindingJob;
import org.nnsoft.guice.autobind.install.bindjob.ConstantBindingJob;
import org.nnsoft.guice.autobind.install.bindjob.ImplementationBindingJob;
import org.nnsoft.guice.autobind.install.bindjob.InstanceBindingJob;
import org.nnsoft.guice.autobind.install.bindjob.InterfaceBindingJob;
import org.nnsoft.guice.autobind.install.bindjob.ProviderBindingJob;
import org.nnsoft.guice.autobind.scanner.ScannerModule;
import org.nnsoft.guice.autobind.utils.VariableResolver;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.util.Providers;

/**
 * Default Implementation for Annotation Listeners, which should stay informed
 * abbout found annotated classes. Due the fact, that we need the Binder of the
 * Child Injector, it will be set at runtime by the {@link ScannerModule}.
 */
public abstract class BindingScannerFeature
    implements ScannerFeature
{

    private final Logger _logger = getLogger( getClass().getName() );

    protected Set<String> others = new HashSet<String>();

    protected Set<String> qualifiers = new HashSet<String>();

    protected Binder _binder;

    @Inject
    protected Injector injector;

    @Inject
    protected BindingTracer tracer;

    @Inject
    protected VariableResolver resolver;

    protected InstallationContext context;

    public void setBinder( Binder binder )
    {
        _binder = binder;
    }

    @Inject
    public void configure( InstallationContext context )
    {
        this.context = context;
    }

    @Override
    public void found( final Class<Object> annotatedClass, final Map<String, Annotation> annotations )
    {
        final BindingStage stage = accept( annotatedClass, annotations );
        if ( stage != BindingStage.IGNORE )
        {
            context.add( new StageableRequest()
            {
                private Class<Object> _annotatedClass = annotatedClass;

                private Map<String, Annotation> _annotations = new HashMap<String, Annotation>( annotations );

                @Override
                public Void call()
                    throws Exception
                {
                    process( _annotatedClass, _annotations );
                    return null;
                }

                @Override
                public BindingStage getExecutionStage()
                {
                    return stage;
                }
            } );
        }
    }

    public abstract BindingStage accept( Class<Object> annotatedClass, Map<String, Annotation> annotations );

    public abstract void process( Class<Object> annotatedClass, Map<String, Annotation> annotations );

    protected <T, V extends T> void bindProvider( final Provider<V> provider, Class<T> interf, Annotation annotation,
                                                  Class<? extends Annotation> scope )
    {
        BindingJob job = new ProviderBindingJob( scope, provider.getClass(), annotation, interf.getName() );
        if ( !tracer.contains( job ) )
        {
            LinkedBindingBuilder<T> builder;
            synchronized ( _binder )
            {
                builder = _binder.bind( interf );
                if ( annotation != null )
                {
                    builder = ( (AnnotatedBindingBuilder<T>) builder ).annotatedWith( annotation );
                }
                ScopedBindingBuilder scopedBuilder = builder.toProvider( Providers.guicify( provider ) );
                if ( scope != null )
                {
                    scopedBuilder.in( scope );
                }
            }
            tracer.add( job );
        }
        else
        {
            String message = format( "Ignoring BindingJob \"%s\", because it was already bound.", job );

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

    protected <T, V extends T> void bindProvider( final Class<? extends Provider<V>> provider, Class<T> interf,
                                                  Annotation annotation, Class<? extends Annotation> scope )
    {
        BindingJob job = new ProviderBindingJob( scope, provider, annotation, interf.getName() );
        if ( !tracer.contains( job ) )
        {
            LinkedBindingBuilder<T> builder;
            synchronized ( _binder )
            {
                builder = _binder.bind( interf );
                if ( annotation != null )
                {
                    builder = ( (AnnotatedBindingBuilder<T>) builder ).annotatedWith( annotation );
                }
                ScopedBindingBuilder scopedBuilder = builder.toProvider( provider );
                if ( scope != null )
                {
                    scopedBuilder.in( scope );
                }
            }
            tracer.add( job );
        }
        else
        {
            String message = format( "Ignoring BindingJob \"%s\", because it was already bound.", job );

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

    protected <T, V extends T> void bindInstance( V implementation, Class<T> interf, Annotation annotation,
                                                  Class<? extends Annotation> scope )
    {
        BindingJob job =
            new InstanceBindingJob( scope, annotation, implementation.getClass().getName(), interf.getName() );

        if ( !tracer.contains( job ) )
        {
            LinkedBindingBuilder<T> builder;
            synchronized ( _binder )
            {
                builder = _binder.bind( interf );
                if ( annotation != null )
                {
                    builder = ( (AnnotatedBindingBuilder<T>) builder ).annotatedWith( annotation );
                }
                builder.toInstance( implementation );
            }
            tracer.add( job );
        }
        else
        {
            String message = format( "Ignoring BindingJob \"%s\", because it was already bound.", job );

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

    protected void bindConstant( String value, Annotation annotation )
    {
        BindingJob job = new ConstantBindingJob( annotation, value.getClass().getName() );
        if ( !tracer.contains( job ) )
        {
            synchronized ( _binder )
            {
                _binder.bindConstant().annotatedWith( annotation ).to( value );
            }
            tracer.add( job );
        }
        else
        {
            String message = format( "Ignoring BindingJob \"%s\", because it was already bound.", job );

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

    protected <T, V extends T> void bind( Class<V> implementationClass, Class<T> interf, Annotation annotation,
                                          Class<? extends Annotation> scope )
    {
        BindingJob job = new InterfaceBindingJob( scope, annotation, implementationClass.getName(), interf.getName() );

        if ( !tracer.contains( job ) )
        {
            LinkedBindingBuilder<T> builder;
            synchronized ( _binder )
            {
                builder = _binder.bind( interf );
                if ( annotation != null )
                {
                    builder = ( (AnnotatedBindingBuilder<T>) builder ).annotatedWith( annotation );
                }
                ScopedBindingBuilder scopedBindingBuilder = builder.to( implementationClass );
                if ( scope != null )
                {
                    scopedBindingBuilder.in( scope );
                }
            }
            tracer.add( job );
        }
        else
        {
            String message = format( "Ignoring BindingJob \"%s\", because it was already bound.", job );

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

    protected <T> void bind( Class<T> implementationClass, Annotation annotation, Class<? extends Annotation> scope )
    {
        BindingJob job = new ImplementationBindingJob( scope, annotation, implementationClass.getName() );

        if ( !tracer.contains( job ) )
        {
            LinkedBindingBuilder<T> builder;
            synchronized ( _binder )
            {
                builder = _binder.bind( implementationClass );
                if ( annotation != null )
                {
                    builder = ( (AnnotatedBindingBuilder<T>) builder ).annotatedWith( annotation );
                }
                if ( scope != null )
                {
                    builder.in( scope );
                }
            }
            tracer.add( job );
        }
        else
        {
            String message = format( "Ignoring BindingJob \"%s\", because it was already bound.", job );

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
