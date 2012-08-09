package org.nnsoft.guice.autobind.test.aop;

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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nnsoft.guice.autobind.test.aop.annoherited.AnnoheritedInterceptorTests;
import org.nnsoft.guice.autobind.test.aop.annotated.AnnotatedInterceptorTests;
import org.nnsoft.guice.autobind.test.aop.inherited.InheritedInterceptorTests;
import org.nnsoft.guice.autobind.test.aop.invalid.InvalidInterceptorTests;

@RunWith( Suite.class )
@Suite.SuiteClasses( { AnnoheritedInterceptorTests.class,
                       AnnotatedInterceptorTests.class,
                       InheritedInterceptorTests.class,
                       InvalidInterceptorTests.class } )
public class AllTests
{
}
