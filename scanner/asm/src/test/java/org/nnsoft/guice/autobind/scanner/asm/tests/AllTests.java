
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
package org.nnsoft.guice.autobind.scanner.asm.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.AutobindTests;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.bind.InterfaceAutobindTests;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.duplicate.DuplicateAutobindTests;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.filter.PackageFilterTests;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.multiple.MultibindTests;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.names.NamedAutobindTests;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.only.ImplementationOnlyTests;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.provider.ProviderTest;
import org.nnsoft.guice.autobind.scanner.asm.tests.autobind.startconfig.StartConfigProviderTest;


@RunWith(Suite.class)
@Suite.SuiteClasses( { AutobindTests.class, InterfaceAutobindTests.class,
		DuplicateAutobindTests.class, NamedAutobindTests.class, MultibindTests.class,
		ImplementationOnlyTests.class, PackageFilterTests.class, StartConfigProviderTest.class, ProviderTest.class })
public class AllTests {
}
