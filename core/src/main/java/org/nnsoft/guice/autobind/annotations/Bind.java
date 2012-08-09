package org.nnsoft.guice.autobind.annotations;

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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Named;
import javax.inject.Qualifier;

/**
 * Annotate a Class which should be bound automatically. The Classpath Scanner,
 * will check for these classes. If the name()-Attribute is set (default is ""),
 * the class will be bound to the implemented interfaces and a named annotation.
 *
 * You can overwrite the interfaces, which should be used for binding the class.
 * If bind()-Attribute is not set, the implemented interfaces will be used. If
 * set they will be ignored and overwritten.
 *
 * If you annotate your class with {@link com.google.inject.Singleton} or
 * {@link javax.inject.Singleton} they will be also bound to the
 * Singleton-Scope.
 */
@Qualifier
@GuiceAnnotation
@Retention( RUNTIME )
@Target( { TYPE } )
public @interface Bind
{

    Named value() default @Named( "" );

    boolean multiple() default false;

    AnnotatedWith annotatedWith() default @AnnotatedWith();

    To to() default @To( );

}
