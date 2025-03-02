/*
 * Copyright 2022-2025 Leon Linhart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.osmerion.jvm.launcher.gradle.internal

import com.osmerion.jvm.launcher.gradle.JvmLauncher
import com.osmerion.jvm.launcher.gradle.JvmLauncherExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

internal abstract class JvmLauncherExtensionImpl @Inject constructor(
    objectFactory: ObjectFactory
) : JvmLauncherExtension {

    override val launchers: NamedDomainObjectContainer<JvmLauncher> = objectFactory.domainObjectContainer(JvmLauncher::class.java) { name ->
        objectFactory.newInstance(JvmLauncherImpl::class.java, name)
    }

}