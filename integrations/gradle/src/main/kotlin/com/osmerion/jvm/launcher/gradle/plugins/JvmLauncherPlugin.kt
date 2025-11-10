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
import com.osmerion.jvm.launcher.gradle.internal.BuildConfig
import com.osmerion.jvm.launcher.gradle.internal.JvmLauncherExtensionImpl
import com.osmerion.jvm.launcher.gradle.internal.JvmLauncherImpl
import com.osmerion.jvm.launcher.gradle.internal.tasks.UnzipJvmLauncherSources
import com.osmerion.jvm.launcher.gradle.tasks.BuildJvmLauncher
import com.osmerion.jvm.launcher.gradle.tasks.GenerateLauncherConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.util.GradleVersion
import javax.inject.Inject

/**
 * The `JvmLauncherPlugin` registers the [JvmLauncherExtension] and automatically registers tasks for building JVM
 * launchers.
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
public open class JvmLauncherPlugin @Inject protected constructor() : Plugin<Project> {

    override fun apply(target: Project) {
        if (GradleVersion.current() < GradleVersion.version("9.0.0")) {
            throw IllegalStateException("This plugin requires Gradle 9.0.0 or later")
        }

        val jvmLauncherExtension = target.extensions.create(
            JvmLauncherExtension::class.java,
            "jvmLauncher",
            JvmLauncherExtensionImpl::class.java
        ) as JvmLauncherExtensionImpl

        val sourcesConfiguration = target.configurations.dependencyScope("jvmLauncherSources")
        val resolvableSourcesConfiguration = target.configurations.resolvable("jvmLauncherSourcesResolvable") {
            extendsFrom(sourcesConfiguration.get())
        }

        target.dependencies.add(sourcesConfiguration.name, "com.osmerion.jvmlauncher:jvm-launcher:${BuildConfig.BUILD_VERSION}")

        val pluginOutputDirectory = target.layout.buildDirectory.dir("jvm-launcher")
        val jvmLauncherSourceDirectory = pluginOutputDirectory.map {  it.dir("sources") }

        val unpackJvmLauncherSources = target.tasks.register("unpackJvmLauncherSources", UnzipJvmLauncherSources::class.java) {
            inputFiles.from(resolvableSourcesConfiguration)
            outputDirectory.set(jvmLauncherSourceDirectory)
        }

        val jvmLaunchersDirectory = pluginOutputDirectory.map {  it.dir("launchers") }
        jvmLauncherExtension.launchers.all launcher@{
            val launcherDirectory = jvmLaunchersDirectory.map { it.dir(name) }
            val launcherOutputDirectory = launcherDirectory.map { it.dir("bin") }
            val launcherBuildDirectory = launcherDirectory.map { it.dir("sources") }

            val launcherName = name.replaceFirstChar(Char::uppercaseChar)

            val prepareLauncherSource = target.tasks.register("prepare${launcherName}Sources", Sync::class.java) {
                from(unpackJvmLauncherSources)
                into(launcherBuildDirectory)
            }

            val compileJvmLauncher = target.tasks.register("compile${launcherName}JvmLauncher", BuildJvmLauncher::class.java) {
                dependsOn(prepareLauncherSource)

                this.sourceDirectory.convention(launcherBuildDirectory)
                this.destinationDirectory.convention(launcherOutputDirectory)

                this.fileVersion.convention(this@launcher.fileVersion)
                this.productVersion.convention(this@launcher.productVersion)

                this.originalFilename.convention(this@launcher.name)

                this.stringFileInfo.convention((this@launcher as JvmLauncherImpl).stringFileInfo)

                this.icon.convention(this@launcher.icon)
                this.resources.convention(this@launcher.resources)
            }

            val generateLauncherConfig = target.tasks.register("generate${launcherName}LauncherConfig", GenerateLauncherConfig::class.java) {
                this.outputFile.convention(launcherOutputDirectory.map { it.file("config.toml") })

                this.libjvmPath.convention(this@launcher.libjvmPath)
                this.jvmArgs.convention(this@launcher.jvmArgs)
                this.classpath.convention(this@launcher.classpath)
                this.mainClassName.convention(this@launcher.mainClassName)
            }

            target.tasks.register("build${launcherName}JvmLauncher") {
                dependsOn(compileJvmLauncher, generateLauncherConfig)
            }
        }
    }

}