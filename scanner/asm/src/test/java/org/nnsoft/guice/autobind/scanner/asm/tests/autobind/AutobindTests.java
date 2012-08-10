package org.nnsoft.guice.autobind.scanner.asm.tests.autobind;

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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.scanner.PackageFilter;
import org.nnsoft.guice.autobind.scanner.StartupModule;
import org.nnsoft.guice.autobind.scanner.asm.ASMClasspathScanner;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class AutobindTests
{

    @Test
    public void createDynamicModule()
    {
        Injector injector =
            Guice.createInjector( StartupModule.create( ASMClasspathScanner.class,
                                                        PackageFilter.create( AutobindTests.class, false ) ) );
        assertNotNull( injector );
    }

    @Test
    public void testWithWrongPackage()
    {
        Injector injector =
            Guice.createInjector( StartupModule.create( ASMClasspathScanner.class, PackageFilter.create( "java", false ) ) );
        assertNotNull( injector );

        TestInterface testInstance;
        try
        {
            testInstance = injector.getInstance( TestInterface.class );
            fail( "The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "
                + ( testInstance == null ) );
        }
        catch ( ConfigurationException e )
        {
            // ok
        }
    }

    @Test
    public void createTestInterface()
    {
        Injector injector =
            Guice.createInjector( StartupModule.create( ASMClasspathScanner.class,
                                                        PackageFilter.create( AutobindTests.class, false ) ) );
        assertNotNull( injector );

        TestInterface testInstance = injector.getInstance( TestInterface.class );
        assertNotNull( testInstance );
        assertTrue( testInstance.sayHello().equals( TestInterfaceImplementation.TEST ) );
        assertTrue( testInstance instanceof TestInterfaceImplementation );
        assertTrue( testInstance instanceof SecondTestInterface );
    }

    @Test
    public void createSecondTestInterface()
    {
        Injector injector =
            Guice.createInjector( StartupModule.create( ASMClasspathScanner.class,
                                                        PackageFilter.create( AutobindTests.class, false ) ) );
        assertNotNull( injector );

        SecondTestInterface sameInstance = injector.getInstance( SecondTestInterface.class );
        assertNotNull( sameInstance );
        assertTrue( sameInstance.fireEvent().equals( TestInterfaceImplementation.EVENT ) );
        assertTrue( sameInstance instanceof TestInterfaceImplementation );
        assertTrue( sameInstance instanceof TestInterface );
    }

    @Test
    public void createAllInterfaces()
    {
        Injector injector =
            Guice.createInjector( StartupModule.create( ASMClasspathScanner.class,
                                                        PackageFilter.create( AutobindTests.class, false ) ) );
        assertNotNull( injector );

        TestInterface testInstance = injector.getInstance( TestInterface.class );
        assertNotNull( testInstance );
        assertTrue( testInstance.sayHello().equals( TestInterfaceImplementation.TEST ) );
        assertTrue( testInstance instanceof TestInterfaceImplementation );
        assertTrue( testInstance instanceof SecondTestInterface );

        SecondTestInterface sameInstance = injector.getInstance( SecondTestInterface.class );
        assertNotNull( sameInstance );
        assertTrue( sameInstance.fireEvent().equals( TestInterfaceImplementation.EVENT ) );
        assertTrue( sameInstance instanceof TestInterfaceImplementation );
        assertTrue( testInstance instanceof TestInterface );
    }

    public static interface TestInterface
    {
        String sayHello();
    }

    public static interface SecondTestInterface
    {
        String fireEvent();
    }

    @Bind
    public static class TestInterfaceImplementation
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

}
