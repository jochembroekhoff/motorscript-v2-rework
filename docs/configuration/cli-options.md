As described on the [Introduction](../introduction.md) page, the compiler accepts CLI options (Command Line Interface
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
