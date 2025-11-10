### 0.4.0

_Released 2025 Nov 10_

#### Improvements

- Additional resource files can now be included using the `resources` property.
    - The plugin does not parse resource files, so user's will have to set up
      input tracking for referenced files themselves.


---

### 0.3.1

_Released 2025 Nov 06_

#### Fixes

- `icon.rc` now uses relative paths to prevent issues when resolving absolute
  paths.


---

### 0.3.0

_Released 2025 Aug 12_

#### Fixes

- Removed a leftover debug print from the launcher executable.

#### Breaking Changes

- The Gradle plugin now requires Java 17 (from 11).
- The Gradle plugin now requires at least Gradle 9.0.0 (from 8.0.0).


---

### 0.2.0

_Released 2025 Apr 19_

#### Improvements

- Starting with this version, relative paths in the  `jvm_args` configuration
  property should use the `<path:...>` syntax. Those are resolved at run-time
  against the directory of the launcher executable.
    - This fixes an issue where relative paths were unexpectedly resolved against
      the working directory instead of the launcher executable directory.


---

### 0.1.0

_Released 2025 Mar 08_

#### Overview

Tools to build a customized launcher executables for seamless experiences with
JVM-based desktop applications.