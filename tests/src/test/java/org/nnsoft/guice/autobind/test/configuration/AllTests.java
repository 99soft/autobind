/**
 * Copyright (C) 2010 Daniel Manzke <daniel.manzke@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nnsoft.guice.autobind.test.configuration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nnsoft.guice.autobind.test.configuration.classpath.ClasspathConfigTests;
import org.nnsoft.guice.autobind.test.configuration.classpath.both.BothClasspathConfigTests;
import org.nnsoft.guice.autobind.test.configuration.classpath.values.ValuesClasspathConfigTests;
import org.nnsoft.guice.autobind.test.configuration.duplicate.DuplicateClasspathConfigTests;
import org.nnsoft.guice.autobind.test.configuration.failure.FailureConfigTests;
import org.nnsoft.guice.autobind.test.configuration.file.FileConfigTests;
import org.nnsoft.guice.autobind.test.configuration.file.both.BothFileConfigTests;
import org.nnsoft.guice.autobind.test.configuration.file.override.DirectFileConfigTests;
import org.nnsoft.guice.autobind.test.configuration.file.override.OverrideFileConfigTests;
import org.nnsoft.guice.autobind.test.configuration.file.values.ValueFileConfigTests;
import org.nnsoft.guice.autobind.test.configuration.url.URLConfigTests;
import org.nnsoft.guice.autobind.test.configuration.url.both.BothURLConfigTests;
import org.nnsoft.guice.autobind.test.configuration.url.override.DirectOverrideConfigTests;
import org.nnsoft.guice.autobind.test.configuration.url.override.OverrideConfigTests;
import org.nnsoft.guice.autobind.test.configuration.url.values.ValuesURLConfigTests;


@RunWith(Suite.class)
@Suite.SuiteClasses( { ClasspathConfigTests.class, ValuesClasspathConfigTests.class, BothClasspathConfigTests.class, DuplicateClasspathConfigTests.class,
		FailureConfigTests.class, FileConfigTests.class, ValueFileConfigTests.class, BothFileConfigTests.class, URLConfigTests.class, ValuesURLConfigTests.class, BothURLConfigTests.class,
		DirectOverrideConfigTests.class, OverrideConfigTests.class, DirectFileConfigTests.class,
		OverrideFileConfigTests.class })
public class AllTests {
}
