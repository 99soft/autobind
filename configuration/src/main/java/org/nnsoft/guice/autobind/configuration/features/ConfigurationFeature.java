package org.nnsoft.guice.autobind.configuration.features;

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

import static org.nnsoft.guice.autobind.configuration.Configuration.Type.*;
import static org.nnsoft.guice.autobind.install.BindingStage.*;
import static java.lang.String.format;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.nnsoft.guice.autobind.configuration.Configuration;
import org.nnsoft.guice.autobind.configuration.ConfigurationBindingJob;
import org.nnsoft.guice.autobind.configuration.ConfigurationModule;
import org.nnsoft.guice.autobind.configuration.PathConfig;
import org.nnsoft.guice.autobind.configuration.PropertiesProvider;
import org.nnsoft.guice.autobind.configuration.PropertiesReader;
import org.nnsoft.guice.autobind.install.BindingStage;
import org.nnsoft.guice.autobind.install.bindjob.BindingJob;
import org.nnsoft.guice.autobind.scanner.features.BindingScannerFeature;

/**
 * This class will bind a Properties-Instance or -Provider for each Class
 * annotated with {@link Configuration}.
 */
@Singleton
public class ConfigurationFeature
    extends BindingScannerFeature
{

    private final Logger _logger = getLogger( getClass().getName() );

    @Inject
    private ConfigurationModule module;

    @Override
    public BindingStage accept( Class<Object> annotatedClass, Map<String, Annotation> annotations )
    {
        if ( annotations.containsKey( Configuration.class.getName() ) )
        {
            Configuration config = (Configuration) annotations.get( Configuration.class.getName() );
            if ( Properties.class.isAssignableFrom( config.to() ) )
            {
                return BOOT_BEFORE;
            }
        }
        return IGNORE;
    }

    @Override
    public void process( Class<Object> annotatedClass, Map<String, Annotation> annotations )
    {
        Configuration config = (Configuration) annotations.get( Configuration.class.getName() );
        Named name = config.name();

        URL url = null;
        if ( config.alternative().value().length() > 0 )
        {
            url = findURL( name, config.alternative() );
            if ( url != null )
            {
                try
                {
                    // TODO Use an Executor to test, if the Stream can be opened?
                    // FIXME What happens if Error Page is returned?
                    /*
                     * final URL alternativeURL = url; Future<URL> submit =
                     * Executors.newSingleThreadExecutor().submit(new Callable<URL>() {
                     * @Override public URL call() throws Exception { alternativeURL.openConnection().getInputStream();
                     * return alternativeURL; } }); submit.get(5, TimeUnit.SECONDS);
                     */
                    url.openStream();
                }
                catch ( Exception e )
                {
                    url = null;
                }
            }
        }

        if ( url == null )
        {
            url = findURL( name, config.location() );
        }

        if ( url == null )
        {
            _logger.log( WARNING, format( "Ignoring Configuration %s in %s because is couldn't be found in the Classpath.",
                                          name, config.location() ) );
            // TODO Throw an exception if config doesn't exist?
            return;
        }

        if ( VALUES == config.type() || BOTH == config.type() )
        {
            BindingJob job = new ConfigurationBindingJob( config.name(), url.toString() );
            if ( !tracer.contains( job ) )
            {
                /* && !(url.toString().startsWith("jar:")) */
                _logger.log( INFO, format( "Trying to bind \"%s\" to rocoto Module.", url ) );
                module.addConfigurationReader( new PropertiesURLReader( url, url.toString().endsWith( ".xml" ) ) );
                // TODO do we need protocol handling? file:/, ...
                tracer.add( job );
            }
        }

        if ( CONFIGURATION == config.type() || BOTH == config.type() )
        {
            boolean isXML;
            String path = url.toString();
            if ( path.endsWith( ".xml" ) )
            {
                isXML = true;
            }
            else if ( path.endsWith( ".properties" ) )
            {
                isXML = false;
            }
            else
            {
                _logger.log( WARNING, format( "Ignoring Configuration %s in %s because it doesn't end with .xml or .properties.",
                                              name, config.location() ) );
                // TODO Throw an exception if config has another format?
                return;
            }

            Named named = null;
            if ( name.value().length() > 0 )
            {
                named = name;
            }

            if ( !config.lazy() )
            {
                Properties properties;
                try
                {
                    properties = new PropertiesReader( url, isXML ).readNative();
                }
                catch ( Exception e )
                {
                    _logger.log( WARNING, format( "Configuration %s in %s cannot be loaded: %s",
                                                  name, url, e.getMessage() ), e );
                    return;
                }

                bindInstance( properties, Properties.class, named, null );
            }
            else
            {
                Provider<Properties> provider = new PropertiesProvider( url, isXML );
                bindProvider( provider, Properties.class, named, Singleton.class );
            }
        }
    }

    private URL findURL( Named name, PathConfig config )
    {
        URL url = null;
        String path = resolver.resolve( config.value() );

        switch ( config.type() )
        {
            case FILE:
                File file = new File( path );
                if ( !file.exists() )
                {
                    _logger.log( WARNING, format( "Ignoring Configuration %s in %s, no Configuration found in %s",
                                                  name, path, file.getAbsolutePath() ) );
                    return null;
                }
                if ( file.isFile() )
                {
                    try
                    {
                        url = file.toURI().toURL();
                    }
                    catch ( MalformedURLException e )
                    {
                        _logger.log( WARNING, format( "Ignoring Configuration %s in %s due to illegal URL location",
                                                      name, path ), e );
                        return null;
                    }
                } /*
                   * else if (file.isDirectory()) { for (File entry : file.listFiles()) { try { url =
                   * entry.toURI().toURL(); } catch (MalformedURLException e) { _logger.log(Level.WARNING,
                   * "Ignoring Configuration " + name + " in " + path + ". It has an illegal URL-Format.", e); return
                   * null; } } }
                   */

                break;

            case URL:
                try
                {
                    url = new URL( path );
                }
                catch ( MalformedURLException e )
                {
                    _logger.log( WARNING, format( "Ignoring Configuration %s in %s due to illegal URL location",
                                                  name, path ), e );
                    return null;
                }
                break;

            case CLASSPATH:
            default:
                url = this.getClass().getResource( path );
                break;
        }

        return url;
    }

}
