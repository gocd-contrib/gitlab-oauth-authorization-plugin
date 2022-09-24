# GitLab OAuth authorization plugin for GoCD

The plugin allows user to login in GoCD using GitLab(or GitLab Enterprise). It also supports authorization, which can be used by server admin to map GoCD roles with GitLab projects or groups with access level.

# Installation

Documentation for installation is available [here](INSTALL.md)

# Capabilities

* The plugin is implemented using `GoCD plugin authorization endpoint`. Hence, it supports `authentication` and `authorization`.

## Building the code base

To build the jar, run `./gradlew clean test assemble`

## License

```plain
Copyright 2022 Thoughtworks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
