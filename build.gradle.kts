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
plugins {
    id("com.osmerion.maven-publish-conventions")
}

val jar = tasks.register<Jar>("jar") {
    destinationDirectory.set(layout.buildDirectory.dir("jars"))
    from(layout.projectDirectory.dir("launcher")) {
        into("launcher")

        include("src/**")
        include("build.rs")
        include("Cargo.lock")
        include("Cargo.toml")
    }
}

configurations {
    artifacts {
        add("default", jar)
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            val emptyJar = tasks.register<Jar>("emptyJar") {
                destinationDirectory = layout.buildDirectory.dir("emptyJar")
                archiveBaseName = "com.osmerion.jvm-launcher.gradle.plugin"
            }

            artifact(tasks["jar"])
            artifact(emptyJar) { classifier = "javadoc" }
            artifact(emptyJar) { classifier = "sources" }

            pom {
                name = "JVM Launcher Sources"
                description = "The sources required for building a JVM launcher."
            }
        }
    }
}