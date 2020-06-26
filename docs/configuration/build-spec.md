The `mosbuild.json` file contains the most important configuration. Generally it will be referred to as just 'the build
configuration' or 'build specification', because the configuration in this file mainly concerns the actual build
process.

As the file extension already suggests, the build configuration is expected to be a JSON file.

Example contents of a build configuration file:

```json
{
  "name": "example",
  "version": "1.0.0",
  "description": "MotorScript example",
  "targets": [{
    "platform": "java",
    "version": "1.16.2"
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
