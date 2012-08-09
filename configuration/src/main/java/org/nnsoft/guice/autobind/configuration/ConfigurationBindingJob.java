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

import static com.google.common.base.Objects.toStringHelper;

import java.lang.annotation.Annotation;

import org.nnsoft.guice.autobind.install.bindjob.BindingJob;

public class ConfigurationBindingJob
    extends BindingJob
{

    public ConfigurationBindingJob( Annotation annotated, String location )
    {
        super( null, null, annotated, location, null );
    }

    @Override
    public String toString()
    {
        return toStringHelper( getClass().getSimpleName() )
               .add( "annotated", annotated )
               .add( "className", className )
               .toString();
    }

}
