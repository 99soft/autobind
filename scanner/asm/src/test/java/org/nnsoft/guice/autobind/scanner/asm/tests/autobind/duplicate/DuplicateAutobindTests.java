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
package org.nnsoft.guice.autobind.scanner.asm.tests.autobind.duplicate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.annotations.To;
import org.nnsoft.guice.autobind.annotations.To.Type;
import org.nnsoft.guice.autobind.scanner.PackageFilter;
import org.nnsoft.guice.autobind.scanner.StartupModule;
import org.nnsoft.guice.autobind.scanner.asm.ASMClasspathScanner;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;


public class DuplicateAutobindTests {
	@Test
	public void createDynamicModule() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(DuplicateAutobindTests.class)));
		assertNotNull(injector);
	}

	@Test
	public void testWithWrongPackage() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create("java")));
		assertNotNull(injector);

		try {
			SecondTestInterface testInstance = injector.getInstance(SecondTestInterface.class);
			fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "
					+ (testInstance == null));
		} catch (ConfigurationException e) {
			// ok
		}
	}

	@Test
	public void createTestInterface() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(DuplicateAutobindTests.class)));
		assertNotNull(injector);

		try {
			TestInterface testInstance = injector.getInstance(TestInterface.class);
			fail("Instance implements TestInterface, but was not bound to. Instance may be null? "
					+ (testInstance == null));
		} catch (ConfigurationException e) {
			// ok
		}
	}

	@Test
	public void createSecondTestInterface() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(DuplicateAutobindTests.class)));
		assertNotNull(injector);

		SecondTestInterface sameInstance = injector.getInstance(SecondTestInterface.class);
		assertNotNull(sameInstance);
		assertTrue(sameInstance.fireEvent().equals(TestInterfaceImplementation.EVENT));
		assertTrue(sameInstance instanceof TestInterface);
	}

	public static interface TestInterface {
		String sayHello();
	}

	public static interface SecondTestInterface {
		String fireEvent();
	}

	@Bind(to = @To(value=Type.CUSTOM, customs={SecondTestInterface.class}))
	public static class TestInterfaceImplementation implements TestInterface, SecondTestInterface {
		public static final String TEST = "test";
		public static final String EVENT = "event";

		@Override
		public String sayHello() {
			return TEST;
		}

		@Override
		public String fireEvent() {
			return EVENT;
		}
	}

	@Bind(to = @To(value=Type.CUSTOM, customs={SecondTestInterface.class}))
	public static class SecondInterfaceImplementation implements TestInterface, SecondTestInterface {
		public static final String TEST = "test";
		public static final String EVENT = "event";

		@Override
		public String sayHello() {
			return TEST;
		}

		@Override
		public String fireEvent() {
			return EVENT;
		}
	}
}
