# JVM Launcher Gradle Plugin

[![Maven Central](https://img.shields.io/maven-central/v/com.osmerion.jvmlauncher/jvm-launcher-gradle-plugin.svg?style=for-the-badge&label=Maven%20Central)](https://maven-badges.herokuapp.com/maven-central/com.osmerion.jvmlauncher/jvm-launcher-gradle-plugin)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v.svg?style=for-the-badge&label=Gradle%20Plugin%20Portal&logo=Gradle&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fcom%2Fosmerion%2Fjvm-launcher%2Fcom.osmerion.jvm-launcher.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/com.osmerion.jvm-launcher)
![Gradle](https://img.shields.io/badge/Gradle-9.0.0-green.svg?style=for-the-badge&color=1ba8cb&logo=Gradle)
![Java](https://img.shields.io/badge/Java-17-green.svg?style=for-the-badge&color=b07219&logo=Java)

A Gradle plugin to integrate the creation of custom JVM launchers into the build
process of JVM-based desktop applications.


## Usage

> [!NOTE]
> The documentation of this plugin is written in [Gradle's Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html).
> The plugin can also be used with Groovy build scripts and all concepts still
> apply but the exact syntax may differ.

### Applying the Plugin

```kotlin
plugins {
    id("com.osmerion.jvm-launcher") version "0.1.0"
}
```


### Launchers

Launchers can be registered using the `jvmLauncher` extension:

```kotlin
jvmLauncher {
    launchers {
        register("MyApplication") {
            fileVersion(1, 2, 3, 4)
            productVersion(1, 2, 3, 4)

            stringFileInfo {
                companyName = "My Company"
                fileDescription = "My Description"
                fileVersion = "$version"
                productName = "My Product"
                productVersion = "$version"
            }

            /*
             * Assuming the application directory will be structured as follows:
             * - application/
             *   - jars/ (contains the application)
             *   - runtime/ (contains the JVM)
             *   - config.toml
             *   - MyApplication.exe
             */
            mainClassName = "com/gw2tb/manager/MainKt"
            libjvmPath = "./runtime/bin/server/jvm.dll"

            classpath.add("./jars/example.jar")
        }
    }
}
```

Then, the `buildMyApplicationJvmLauncher` task can be used to build the
launcher. By default, the executable can be found in the `build/jvm-launchers/launchers/MyApplication/bin/`
directory.


## Compatibility

| JDK Tools Gradle Plugin | Supported Gradle versions |
|-------------------------|---------------------------|
| 0.3.0                   | 9.0+                      |
| 0.1.0                   | 8.7+                      |