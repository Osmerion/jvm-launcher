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

import com.osmerion.jvm.launcher.gradle.StringFileInfoBlock
import com.osmerion.jvm.launcher.gradle.VersionNumber
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.inject.Inject

/**
 * TODO doc
 *
 * @since   0.1.0
 *
 * @author  Leon Linhart
 */
@CacheableTask
public open class BuildJvmLauncher @Inject constructor(
    private val execOperations: ExecOperations,
    objectFactory: ObjectFactory
): DefaultTask() {

    @get:Input
    public val executable: Property<String> = objectFactory.property(String::class.java)
        .convention("cargo.exe")

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val sourceDirectory: DirectoryProperty = objectFactory.directoryProperty()

    @get:Internal
    public val destinationDirectory: DirectoryProperty = objectFactory.directoryProperty()

    // fixed-info

    @get:Input
    public val fileVersion: Property<VersionNumber> = objectFactory.property(VersionNumber::class.java)

    @get:Input
    public val productVersion: Property<VersionNumber> = objectFactory.property(VersionNumber::class.java)

    // lifted props

    /**
     * TODO doc
     *
     * @since   0.1.0
     */
    @get:Input
    public val originalFilename: Property<String> = objectFactory.property(String::class.java)

    // props

    @get:Nested
    public val stringFileInfo: Property<StringFileInfoBlock> = objectFactory.property(StringFileInfoBlock::class.java)

    // other

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    public val icon: RegularFileProperty = objectFactory.fileProperty()

    init {
        outputs.file(destinationDirectory.flatMap { it.dir(originalFilename.map { "$it.exe" }) })
        outputs.file(destinationDirectory.flatMap { it.dir(originalFilename.map { "$it.pdb" }) })
    }

    @TaskAction
    protected fun compile() {
        val sourceDirectory = sourceDirectory.get()
        val originalFilename = originalFilename.get()

        val resources = sourceDirectory.file("launcher.rc")

        val versionInfoResource = buildString {
            appendLine("#include <winresrc.h>")
            appendLine("1               VERSIONINFO")
            fileVersion.orNull?.let { appendLine("FILEVERSION     ${it.toString(separator = ",")}") }
            productVersion.orNull?.let { appendLine("PRODUCTVERSION  ${it.toString(separator = ",")}") }
            appendLine("FILEOS          VOS__WINDOWS32")
            appendLine("FILETYPE        VFT_APP")
            appendLine("BEGIN")

            append(buildString {
                appendLine("BLOCK \"StringFileInfo\"")
                appendLine("BEGIN")
                append(buildString {
                    val stringFileInfo = stringFileInfo.get()

                    appendLine("BLOCK \"040904B0\"")
                    appendLine("BEGIN")
                    append(buildString {
                        stringFileInfo.comments.orNull?.let { appendLine("VALUE \"Comments\", \"$it\\0\"") }
                        appendLine("VALUE \"CompanyName\", \"${stringFileInfo.companyName.get()}\\0\"")
                        appendLine("VALUE \"FileDescription\", \"${stringFileInfo.fileDescription.get()}\\0\"")
                        appendLine("VALUE \"FileVersion\", \"${stringFileInfo.fileVersion.get()}\\0\"")
                        appendLine("VALUE \"InternalName\", \"${stringFileInfo.internalName.get()}\\0\"")
                        stringFileInfo.legalCopyright.orNull?.let { appendLine("VALUE \"LegalCopyright\", \"$it\\0\"") }
                        stringFileInfo.legalTrademarks.orNull?.let { appendLine("VALUE \"LegalTrademarks\", \"$it\\0\"") }
                        appendLine("VALUE \"OriginalFilename\", \"$originalFilename.exe\\0\"")
                        appendLine("VALUE \"ProductName\", \"${stringFileInfo.productName.get()}\\0\"")
                        append("VALUE \"ProductVersion\", \"${stringFileInfo.productVersion.get()}\\0\"")
                    }.prependIndent("    ") + "\n")
                    append("END")
                }.prependIndent("    ") + "\n")
                appendLine("END")
                appendLine("BLOCK \"VarFileInfo\"")
                appendLine("BEGIN")
                append(buildString {
                    append("VALUE \"Translation\", 0x409 0x4B0")
                }.prependIndent("    ") + "\n")
                append("END")
            }.prependIndent("    ") + "\n")
            append("END")
        }

        resources.asFile.writeText(versionInfoResource)

        val iconResource = icon.orNull?.let { iconFile ->
            val iconResource = sourceDirectory.file("icon.rc")
            iconResource.asFile.writeText("""appicon ICON "${iconFile.asFile.absolutePath}"""")

            iconResource
        }

        execOperations.exec {
            this.executable = this@BuildJvmLauncher.executable.get()
            this.workingDir = sourceDirectory.asFile

            environment(buildMap {
                put("OSMERION_jvmLauncher_versioninfo", resources.asFile.absolutePath)
                if (iconResource != null) put("OSMERION_jvmLauncher_icon", iconResource.asFile.absolutePath)
            })

            args("build", "--release")
        }

        val sourceDirectoryPath = sourceDirectory.asFile.toPath()

        val executableFileOutput = destinationDirectory.file("$originalFilename.exe").get().asFile.toPath()
            .also { if (!Files.isDirectory(it.parent)) Files.createDirectory(it.parent) }

        val pdbFileOutput = destinationDirectory.file("$originalFilename.pdb").get().asFile.toPath()

        Files.copy(sourceDirectoryPath.resolve("target/release/launcher.exe"), executableFileOutput, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(sourceDirectoryPath.resolve("target/release/launcher.pdb"), pdbFileOutput, StandardCopyOption.REPLACE_EXISTING)
    }

}