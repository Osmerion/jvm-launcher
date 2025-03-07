# jvm-launcher

[![License](https://img.shields.io/badge/license-Apache%202.0-yellowgreen.svg?style=for-the-badge&label=License)](https://github.com/Osmerion/jvm-launcher/blob/master/LICENSE)

Tools to build a customized launcher executables for seamless experiences with
JVM-based desktop applications.


## Why?

Although Window icons and titles can be changed easily using common GUI
libraries, many Java desktop application use the JVM's native launchers
(`java.exe` or `javaw.exe`) to start the application. Neither of them are
customizable which leads to a poor user experience when interacting with the
application through the task manager and other system tools.


## How?

This project implements a custom Java launcher using the JNI invocation API to
launch a JVM. The launcher is small and can easily be compiled with custom
resources to provide a seamless experience for end-users.


## Usage

Currently, the JVM launcher is only available as a Gradle plugin. [Learn more
about the Gradle plugin](/integrations/gradle/README.md)


## Building from source

### Setup

This project uses [Gradle's toolchain support](https://docs.gradle.org/current/userguide/toolchains.html)
to detect and select the JDKs required to run the build. Please refer to the
build scripts to find out which toolchains are requested.

An installed JDK 1.8 (or later) is required to use Gradle.

### Building

Once the setup is complete, invoke the respective Gradle tasks using the
following command on Unix/macOS:

    ./gradlew <tasks>

or the following command on Windows:

    gradlew <tasks>

Important Gradle tasks to remember are:
- `clean`                   - clean build results
- `build`                   - assemble and test the Java library
- `publishToMavenLocal`     - build and install all public artifacts to the
                              local maven repository

Additionally `tasks` may be used to print a list of all available tasks.


## License

```
Copyright 2022-2025 Leon Linhart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```