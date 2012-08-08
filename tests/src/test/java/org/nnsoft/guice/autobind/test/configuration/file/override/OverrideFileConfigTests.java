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
package org.nnsoft.guice.autobind.test.configuration.file.override;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import junit.framework.Assert;

import org.junit.Test;
import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.configuration.Configuration;
import org.nnsoft.guice.autobind.configuration.PathConfig;
import org.nnsoft.guice.autobind.configuration.PathConfig.PathType;
import org.nnsoft.guice.autobind.configuration.features.ConfigurationFeature;
import org.nnsoft.guice.autobind.scanner.PackageFilter;
import org.nnsoft.guice.autobind.scanner.StartupModule;
import org.nnsoft.guice.autobind.scanner.asm.ASMClasspathScanner;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class OverrideFileConfigTests {
	@Test
	public void createDynamicModule() {
		StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(OverrideFileConfigTests.class));
		startup.addFeature(ConfigurationFeature.class);

		Injector injector = Guice.createInjector(startup);
		assertNotNull(injector);
	}

	@Test
	public void createPListConfiguration() {
		StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(OverrideFileConfigTests.class));
		startup.addFeature(ConfigurationFeature.class);

		Injector injector = Guice.createInjector(startup);
		assertNotNull(injector);

		TestInterface instance = injector.getInstance(TestInterface.class);
		Assert.assertTrue(instance.sayHello(), "sayHello() - overriden yeahh!!".equals(instance
			.sayHello()));
	}

	@Configuration(name = @Named("override"), location = @PathConfig(value = "src/test/resources/configuration.properties", type = PathType.FILE), alternative = @PathConfig(value = "src/test/resources/configuration.override.properties", type = PathType.FILE))
	public interface OverrideConfiguration {
	}

	public static interface TestInterface {
		String sayHello();
	}

	@Bind
	public static class DirectImplementations implements TestInterface {
		@Inject
		@Named("override")
		private Properties config;

		@Override
		public String sayHello() {
			return "sayHello() - " + config.getProperty("message");
		}
	}
}
