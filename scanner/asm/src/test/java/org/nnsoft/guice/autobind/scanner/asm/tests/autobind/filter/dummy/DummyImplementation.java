package org.nnsoft.guice.autobind.scanner.asm.tests.autobind.filter.dummy;

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

import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.filter.PackageFilterTests.SecondTestInterface;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.filter.PackageFilterTests.TestInterface;

@Bind
public class DummyImplementation
    implements TestInterface, SecondTestInterface
{

    public static final String TEST = "test";

    public static final String EVENT = "event";

    @Override
    public String sayHello()
    {
        return TEST;
    }

    @Override
    public String fireEvent()
    {
        return EVENT;
    }

}
