package org.nnsoft.guice.autobind.scanner.features;

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

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * This Interface is used, if you want get informed, for Classes with
 * Annotations. This is used for creating Classes for the automatic Module
 * installation or the automatic Bean binding.
 *
 * You will get the Class for the annotated one and a Proxy of the attached
 * Annotations.
 */
public interface ScannerFeature
{

    void found( Class<Object> annotatedClass, Map<String, Annotation> annotations );

}
