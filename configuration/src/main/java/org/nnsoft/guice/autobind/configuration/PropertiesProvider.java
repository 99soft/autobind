package org.nnsoft.guice.autobind.configuration;

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

import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import javax.inject.Provider;

public class PropertiesProvider
    implements Provider<Properties>
{

    private final Logger _logger = getLogger( getClass().getName() );

    private URL url;

    private boolean isXML;

    public PropertiesProvider( URL url, boolean isXML )
    {
        super();
        this.url = url;
        this.isXML = isXML;
    }

    @Override
    public Properties get()
    {
        try
        {
            _logger.info( "Doing lazy Loading for Configuration " + url );
            return new PropertiesReader( url, isXML ).readNative();
        }
        catch ( Exception e )
        {
            _logger.log( WARNING, "Configuration in " + url + " couldn't be read.", e );
            return new Properties();
        }
    }

}
