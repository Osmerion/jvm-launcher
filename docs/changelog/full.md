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