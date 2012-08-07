package de.devsurf.injection.guice.install;

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

import java.util.LinkedList;
import java.util.List;

public enum BindingStage
{

    INTERNAL,
    BOOT_BEFORE,
    BOOT,
    BOOT_POST,
    BINDING_BEFORE,
    BINDING,
    BINDING_POST,
    INSTALL_BEFORE,
    INSTALL,
    INSTALL_POST,
    BUILD_BEFORE,
    BUILD,
    BUILD_POST,
    IGNORE;

    public static final List<BindingStage> ORDERED = new LinkedList<BindingStage>();

    static
    {
        ORDERED.add( INTERNAL );
        ORDERED.add( BOOT_BEFORE );
        ORDERED.add( BOOT );
        ORDERED.add( BOOT_POST );
        ORDERED.add( BINDING_BEFORE );
        ORDERED.add( BINDING );
        ORDERED.add( BINDING_POST );
        ORDERED.add( INSTALL_BEFORE );
        ORDERED.add( INSTALL );
        ORDERED.add( INSTALL_POST );
        ORDERED.add( BUILD_BEFORE );
        ORDERED.add( BUILD );
        ORDERED.add( BUILD_POST );
    }

}
