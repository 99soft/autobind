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
import static org.nnsoft.guice.autobind.install.BindingStage.BUILD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.nnsoft.guice.autobind.install.BindingStage;

/**
 * Annotate a Module with the GuiceModule-Annotation and it will be installed
 * automatically.
 */
@Qualifier
@GuiceAnnotation
@Target( { TYPE })
@Retention(RUNTIME)
public @interface GuiceModule
{

    BindingStage stage() default BUILD;

}
