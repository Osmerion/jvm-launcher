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
import com.osmerion.jvm.launcher.gradle.StringFileInfoBlock
import com.osmerion.jvm.launcher.gradle.VersionNumber
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

internal abstract class JvmLauncherImpl @Inject constructor(
    private val objectFactory: ObjectFactory
) : JvmLauncher {

    override fun fileVersion(h1: Short, l1: Short, h2: Short, l2: Short) {
        fileVersion.set(VersionNumber(h1, l1, h2, l2))
    }

    override fun productVersion(h1: Short, l1: Short, h2: Short, l2: Short) {
        productVersion.set(VersionNumber(h1, l1, h2, l2))
    }

    @get:Input
    internal val stringFileInfo: Property<StringFileInfoBlock> = objectFactory.property(StringFileInfoBlock::class.java)

    override fun stringFileInfo(action: Action<StringFileInfoBlock>) {
        val block = objectFactory.newInstance(StringFileInfoBlock::class.java)
            .also(action::execute)

        stringFileInfo.set(block)
    }

}