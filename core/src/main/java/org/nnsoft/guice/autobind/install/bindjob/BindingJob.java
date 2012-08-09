package org.nnsoft.guice.autobind.install.bindjob;

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

import static com.google.common.base.Objects.equal;

import java.lang.annotation.Annotation;

import javax.inject.Provider;

@SuppressWarnings( "rawtypes" )
public class BindingJob
{

    public final Class<? extends Annotation> scoped;

    public final Class<? extends Provider> provided;

    public final Annotation annotated;

    public final String className;

    public final String interfaceName;

    public BindingJob( Class<? extends Annotation> scoped, Class<? extends Provider> provided, Annotation annotated,
                       String className, String interfaceName )
    {
        this.scoped = scoped;
        this.provided = provided;
        this.annotated = annotated;
        this.className = className;
        this.interfaceName = interfaceName;
    }

    @Override
    public int hashCode()
    {
        return hashCode( annotated, className, interfaceName, provided, scoped );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null || getClass() != obj.getClass() )
        {
            return false;
        }
        BindingJob other = (BindingJob) obj;

        return equal( annotated, other.annotated )
            && equal( interfaceName, other.interfaceName )
            && equal( className, other.className )
            && equal( provided, other.provided )
            && equal( scoped, other.scoped );
    }

    protected static int hashCode( Object...args )
    {
        final int prime = 31;
        int result = 1;

        for ( Object arg : args )
        {
            result = prime * result + ( ( arg == null ) ? 0 : arg.hashCode() );
        }

        return result;
    }

    @Override
    public String toString()
    {
        return "BindingJob [annotated=" + annotated + ", className=" + className + ", interfaceName=" + interfaceName
            + ", provided=" + provided + ", scoped=" + scoped + "]";
    }

}
