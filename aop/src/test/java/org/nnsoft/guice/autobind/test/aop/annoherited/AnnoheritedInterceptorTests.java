package org.nnsoft.guice.autobind.test.aop.annoherited;

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

import java.lang.reflect.Method;

import javax.interceptor.Interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.nnsoft.guice.autobind.annotations.Bind;
import org.nnsoft.guice.autobind.aop.ClassMatcher;
import org.nnsoft.guice.autobind.aop.Intercept;
import org.nnsoft.guice.autobind.aop.MethodMatcher;
import org.nnsoft.guice.autobind.aop.feature.InterceptorFeature;
import org.nnsoft.guice.autobind.scanner.PackageFilter;
import org.nnsoft.guice.autobind.scanner.StartupModule;
import org.nnsoft.guice.autobind.scanner.asm.ASMClasspathScanner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

public class AnnoheritedInterceptorTests
{

    private static ThreadLocal<Boolean> called = new ThreadLocal<Boolean>();

    @Test
    public void createDynamicModule()
    {
        StartupModule startup =
            StartupModule.create( ASMClasspathScanner.class, PackageFilter.create( AnnoheritedInterceptorTests.class ) );
        startup.addFeature( InterceptorFeature.class );

        Injector injector = Guice.createInjector( startup );
        assertNotNull( injector );
    }

    @Test
    public void createInheritedInterceptor()
    {
        called.set( false );
        StartupModule startup =
            StartupModule.create( ASMClasspathScanner.class, PackageFilter.create( AnnoheritedInterceptorTests.class ) );
        startup.addFeature( InterceptorFeature.class );

        Injector injector = Guice.createInjector( startup );
        assertNotNull( injector );

        TestInterface instance = injector.getInstance( TestInterface.class );
        instance.sayHello(); // should be intercepted
        instance.sayGoodBye(); // if intercepted an exception is thrown

        assertTrue( "Interceptor was not invoked", called.get() );
    }

    @Interceptor
    public static class InheritedMethodInterceptor
        implements MethodInterceptor
    {

        @Override
        public Object invoke( MethodInvocation invocation )
            throws Throwable
        {
            assertTrue( invocation.getMethod().getName().equals( "sayHello" ) );
            called.set( true );
            return invocation.proceed();
        }

        @ClassMatcher
        public Matcher<? super Class<?>> getClassMatcher()
        {
            return Matchers.any();
        }

        @MethodMatcher
        public Matcher<? super Method> getMethodMatcher()
        {
            return Matchers.annotatedWith( Intercept.class );
        }

    }

    public static interface TestInterface
    {

        String sayHello();

        String sayGoodBye();

    }

    @Bind
    public static class TestInterfaceImplementation
        implements TestInterface
    {
        public static final String TEST = "test";

        @Override
        @Intercept
        public String sayHello()
        {
            return TEST;
        }

        @Override
        public String sayGoodBye()
        {
            return "Good Bye!";
        }
    }

}
