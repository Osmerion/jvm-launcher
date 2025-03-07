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
package com.osmerion.jvm.launcher.gradle.plugins

import org.gradle.api.JavaVersion
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

class JvmLauncherPluginTest {

    private companion object {

        @JvmStatic
        private fun provideGradleVersions(): List<String> = buildList {
            // See https://docs.gradle.org/current/userguide/compatibility.html
            val javaVersion = JavaVersion.current()

            add("8.13")
            add("8.12.1")
            add("8.11.1")
            add("8.10.2")

            if (javaVersion >= JavaVersion.VERSION_23) return@buildList

            add("8.9")

            if (javaVersion >= JavaVersion.VERSION_22) return@buildList

            add("8.8")
            add("8.7")
            add("8.6")
            add("8.5")

            if (javaVersion >= JavaVersion.VERSION_21) return@buildList

            add("8.4")
            add("8.3")

            if (javaVersion >= JavaVersion.VERSION_20) return@buildList

            add("8.2.1")
            add("8.1.1")
            add("8.0.2")
        }

        @JvmStatic
        private fun provideSamples(): List<String> = buildList {
            add("hello-gradle")
        }

        @JvmStatic
        private fun provideTestArguments(): List<Arguments> = provideGradleVersions().flatMap { gradleVersion ->
            provideSamples().map { sample -> Arguments.of(gradleVersion, sample) }
        }

    }

    @field:TempDir
    lateinit var projectDir: Path

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun test(gradleVersion: String, sample: String) {
        @OptIn(ExperimentalPathApi::class)
        Paths.get("../../samples", sample).copyToRecursively(target = projectDir, followLinks = true)

        val initScriptPath = projectDir.resolve("init.gradle.kts")
        initScriptPath.writeText(
            Paths.get("./src/functionalTest/init.gradle.kts")
                .readText()
                .replace("{{PLACEHOLDER}}", Paths.get("../../build/functional-test-repo").absolutePathString().replace("\\", "\\\\"))
        )

        GradleRunner.create()
            .withArguments("buildMyApplicationJvmLauncher", "--info", "-S", "--init-script", initScriptPath.absolutePathString())
            .withGradleVersion(gradleVersion)
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .forwardOutput()
            .build()

        assertTrue(projectDir.resolve("build/jvm-launcher/launchers/MyApplication/bin/MyApplication.exe").toFile().exists())
    }

}