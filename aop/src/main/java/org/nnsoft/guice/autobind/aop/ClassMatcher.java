package org.nnsoft.guice.autobind.aop;

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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * This Annotation marks a Method, which returns an Object of Type
 * {@link Matcher}. This Matcher is used by Guice, to decide if a
 * {@link MethodInterceptor} should be invoked for that {@link Class}.
 *
 * <pre>ClassMatcher public Matcher<? super Class<?>> getClassMatcher()
 * {
 *     return Matchers.any();
 * }</pre>
 */
@Documented
@Retention( RUNTIME )
@Target( { METHOD } )
public @interface ClassMatcher
{
}
