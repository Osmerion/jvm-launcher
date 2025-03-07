/*
 * Guild Wars 2 Add-on Manager
 * Copyright (C) 2024-2025 Leon Linhart
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of version 3 of the GNU Lesser General Public License as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.osmerion.jvm.launcher.gradle.internal.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@CacheableTask
internal abstract class UnzipJvmLauncherSources @Inject constructor(
    private val archiveOperations: ArchiveOperations,
    private val fsOperations: FileSystemOperations
) : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    protected fun unzip() {
        val inputFile = inputFiles.single()

        fsOperations.sync {
            from(archiveOperations.zipTree(inputFile)) {
                include("launcher/**")

                includeEmptyDirs = false

                eachFile {
                    if (relativeSourcePath.startsWith("launcher/")) {
                        relativePath = RelativePath(true, *relativeSourcePath.segments.drop(1).toTypedArray())
                    } else {
                        exclude()
                    }
                }
            }
            into(outputDirectory)
        }
    }

}