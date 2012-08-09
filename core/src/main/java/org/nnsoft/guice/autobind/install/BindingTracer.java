package org.nnsoft.guice.autobind.install;

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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Singleton;

import org.nnsoft.guice.autobind.install.bindjob.BindingJob;

@Singleton
public class BindingTracer
    implements Iterable<BindingJob>
{

    private Set<BindingJob> jobs = new HashSet<BindingJob>();

    public synchronized boolean add( BindingJob e )
    {
        return jobs.add( e );
    }

    public synchronized boolean contains( Object o )
    {
        return jobs.contains( o );
    }

    public Iterator<BindingJob> iterator()
    {
        return jobs.iterator();
    }

}
