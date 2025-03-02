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
package com.osmerion.jvm.launcher.gradle.plugins

import com.osmerion.jvm.launcher.gradle.JvmLauncherExtension
import com.osmerion.jvm.launcher.gradle.internal.JvmLauncherExtensionImpl
import com.osmerion.jvm.launcher.gradle.internal.JvmLauncherImpl
import com.osmerion.jvm.launcher.gradle.tasks.BuildJvmLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.tasks.Copy
import javax.inject.Inject

/**
 * TODO doc
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public open class JvmLauncherPlugin @Inject protected constructor(
    private val archiveOperations: ArchiveOperations
) : Plugin<Project> {

    override fun apply(target: Project) {
        val jvmLauncherExtension = target.extensions.create(
            JvmLauncherExtension::class.java,
            "jvmLauncher",
            JvmLauncherExtensionImpl::class.java
        ) as JvmLauncherExtensionImpl

        val sourcesConfiguration = target.configurations.dependencyScope("jvmLauncherSources")
        val resolvableSourcesConfiguration = target.configurations.resolvable("jvmLauncherSourcesResolvable") {
            extendsFrom(sourcesConfiguration.get())
        }

        target.dependencies.add(sourcesConfiguration.name, "com.osmerion.jvmlauncher:jvm-launcher:0.1.0") // TODO Remove hardcoded version

        val jvmLauncherSourceDirectory = target.layout.buildDirectory.dir("jvmLauncherSources").get()

        val unpackJvmLauncherSources = target.tasks.register("unpackJvmLauncherSources", Copy::class.java) {
            dependsOn(resolvableSourcesConfiguration.get())

            into(jvmLauncherSourceDirectory)

            val resolvedFiles = resolvableSourcesConfiguration.get().resolvedConfiguration.files
            doFirst {
                from(archiveOperations.zipTree(resolvedFiles.single()))
            }
        }

        jvmLauncherExtension.launchers.all launcher@{
            target.tasks.register("compileJvmLauncher", BuildJvmLauncher::class.java) {
                dependsOn(unpackJvmLauncherSources)

                // TODO Lock this somehow or copy it to make it parallel-safe
                this.sourceDirectory.convention(jvmLauncherSourceDirectory)

                this.fileVersion.convention(this@launcher.fileVersion)
                this.productVersion.convention(this@launcher.productVersion)

                this.stringFileInfo.convention((this@launcher as JvmLauncherImpl).stringFileInfo)

                this.icon.convention(this@launcher.icon)
            }
        }
    }

}