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
package org.nnsoft.guice.autobind.configuration.example.commons.plist;

import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.annotations.GuiceModule;
import org.nnsoft.guice.autobind.example.starter.ExampleApplication;
import org.nnsoft.guice.autobind.integrations.commons.configuration.CommonsConfigurationFeature;
import org.nnsoft.guice.autobind.scanner.ClasspathScanner;
import org.nnsoft.guice.autobind.scanner.PackageFilter;
import org.nnsoft.guice.autobind.scanner.ScannerModule;
import org.nnsoft.guice.autobind.scanner.StartupModule;
import org.nnsoft.guice.autobind.scanner.asm.ASMClasspathScanner;

import com.google.inject.Guice;
import com.google.inject.Injector;


/**
 * Example Application, which creates a new Injector with the help of the
 * provided {@link StartupModule}. It passes the {@link ASMClasspathScanner}
 * class for the {@link ClasspathScanner} and the packages (de.devsurf) which
 * should be scanned. The {@link StartupModule} binds these parameter, so we are
 * able to create and inject our {@link ScannerModule}. This Module uses the
 * {@link ClasspathScanner} to explore the Classpath and scans for Annotations.
 * 
 * All recognized Classes annotated with {@link GuiceModule} are installed in
 * the child injector and with {@link Bind} are automatically bound.
 * 
 * @author Daniel Manzke
 * 
 */
@Bind(multiple = true)
public class ExampleApp implements ExampleApplication {
	@Override
	public void run() {
		StartupModule startupModule = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(ExampleApp.class));
		startupModule.addFeature(CommonsConfigurationFeature.class);
		Injector injector = Guice.createInjector(startupModule);

		System.out.println(injector.getInstance(Example.class).sayHello());
	}

	public static void main(String[] args) {
		new ExampleApp().run();
	}
}
