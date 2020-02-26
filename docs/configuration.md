# `mosbuid.json`

The `mosbuild.json` file contains the most important configuration. Generally it will be referred to as just 'the build
configuration' or 'build specification', because the configuration in this file mainly concerns the actual build
process.

As the file extension already suggests, the build configuration is expected to be a JSON file.

Example contents of a build configuration file:

```json5
{
  "name": "example",
  "version": "1.0.0",
  "description": "MotorScript example",
  "targets": [{
    "platform": "java",
    "version": "1.15.2"
  }]
}
```

## `example`

Type: string. Required.

Name of the project. This name is used to distinguish the project amongst other projects if it would be used as a
dependency. Try to give it clear but unique name.

## `version`

Type: string. Required.

Project version. Must conform to the SemVer specification.

## `description`

Type: string. Required.

Project description. Currently only used as the description for the generated data pack.

## `targets`

Type: array of objects. Required.

Array of compilation targets. Each object in this array should contain a `platform` and `version` key to indicate to
which platform(s) and version(s) of that the source code should be compiled.
Note that features that _are_ available for a recent of a platform might not be available for older versions of that
certain platform.

(TODO) link to details about possible targets.

## `dependencies`

Type: array of objects. Optional.

Array of dependencies. Defaults to an empty array. Each object in this array should contain a `name` key and optionally
a `version` key. The `name` specifies the name of the dependency, the `version` specifies a version constraint.

**NOTE**: Except for the dependency of the name `stdlib`, nothing actually works right now. That is simply because there
is no final idea about how the dependency system actually would work.

# CLI options

As described on the [Introduction](./introduction.md) page, the compiler accepts CLI options (Command Line Interface
options). In the following subsections, all of the possible CLI options are described.

Every option can be specified in two ways, one is the full name of the option (prefixed by the double dash), the other
possibility is using a shorthand notation, which is a single dash (`-`) followed by one single character.

**NOTE**: Although it may look like these CLI options follow the GNU utilities CLI options conventions, they do not.
It is for example currently not possible to specify the shorthand option immediately followed by a value.
Shorthand clustering is also not supported. Please be aware of this.

## `--buildroot` / `-r`

Default value: `.` (working directory).

This specifies the path to the build root. The build root is the root folder in which the build configuration file and
source files are present. See the section about the project structure for more details about this.

## `--jobs` / `-j`

Default value: `0`.

This specifies the amount of concurrent jobs that the compiler is allowed to use.
If the value is less than 1, the amount of logical processor cores is used as the amount of allowed concurrent jobs.

## `--property` / `-P`

Key-value option, specified as follows: `-P <key>=<value>`.

Currently there is no information available about which keys are used and which respective values are accepted.

## `--debug_flag` / `-d`

Possible values: `export_graph_front`.
Can be specified more than once, duplicates have no effect.
