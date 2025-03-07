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
package com.osmerion.jvm.launcher.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Generates the configuration file for a launcher executable.
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
@CacheableTask
public abstract class GenerateLauncherConfig : DefaultTask() {

    /**
     * The classpath to use when launching the JVM.
     *
     * @since   0.1.0
     */
    @get:Input
    public abstract val classpath: ListProperty<String>

    /**
     * The JVM arguments to pass to the JVM.
     *
     * @since   0.1.0
     */
    @get:Input
    public abstract val jvmArgs: ListProperty<String>

    /**
     * The path to the JVM library.
     *
     * @since   0.1.0
     */
    @get:Input
    public abstract val libjvmPath: Property<String>

    /**
     * The main class to launch.
     *
     * @since   0.1.0
     */
    @get:Input
    public abstract val mainClassName: Property<String>

    /**
     * The output file to write the generated configuration to.
     *
     * @since   0.1.0
     */
    @get:OutputFile
    public abstract val outputFile: RegularFileProperty

    @TaskAction
    protected fun generate() {
        val classpath = classpath.get()

        val jvmArgs = buildList {
            addAll(jvmArgs.get())

            if (classpath.isNotEmpty()) {
                add("-Djava.class.path=${classpath.joinToString(separator = ";")}")
            }
        }

        val libjvmPath = libjvmPath.get()
        val mainClassName = mainClassName.get()

        val outputFile = outputFile.get().asFile

        // Behold! The best TOML "serializer" you'll ever encounter!
        val content = buildString {
            appendLine("libjvm_path = \"$libjvmPath\"")
            appendLine("main_class = \"$mainClassName\"")

            append("jvm_args = [")

            val jvmArgsString = jvmArgs.joinToString(separator = ",\n") { "\"$it\"" }
            if (jvmArgsString.isNotEmpty()) {
                append("\n")
                append(jvmArgsString.prependIndent(indent = "    "))
                append("\n")
            }

            append("]")
        }

        outputFile.writeText(content)
    }

}