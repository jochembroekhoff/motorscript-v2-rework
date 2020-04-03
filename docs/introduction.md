# Usage requirements

MotorScript runs on all major platforms, including Linux. There are no other programs required to use MotorScript.

Ready-to-use builds are available for Linux and Windows.

# Project structure

Example directory structure:

```
mosbuild.json           <-- build configuration
src/
    example/            <-- namespace
        mos/            <-- type
            hello.mos   <-- source file
```

This directory structure is nearly identical to the structure used in a Minecraft [data pack][MCWiki/Data_pack], except
that the `data` directory is called `src` instead. Also the `pack.mcmeta` file is not part of a MotorScript project
structure, instead, a file with a slightly comparable functionality is there, called `mosbuid.json`.
See the [Build spec](./configuration/build-spec.md) page for more information about that file.

In this case, the namespace folder was called `example`, but this may be any name, as long as it adheres to the
Minecraft namespaced identifier requirements.

**FUTURE**: It might be possible at some point to have non-MotorScript resources inside the project source folder.
All those resources could then just be copied to the destination data pack.

# Running the compiler

Running the compiler is core to using MotorScript. Before being able to physically run any code you wrote, it has to go
through the compiler. Once MotorScript has been installed, the compiler should be available with the `mosc` command.
For the reset of this section, it is assumed MotorScript Compiler is available by the `mosc` command.

```
$ ./mosc [option...]
```

See [CLI options](./configuration/cli-options.md) for details about which options are available and what they do.

As can be seen from the list of available options, it is not possible to directly compile a stand-alone MotorScript
file. All source files will be discovered automatically, they are scanned in the `src` directory.

Once the process hopefully returns and exits after a short while, you should see some output having been produced. The
output produced by the compiler gives you insight into what errors the compiler encountered and which warnings were
emitted. The messages are designed to be as human-readable as possible. Although they're far away from being as helpful
as Rust's error messages, they do try to give you a relatively detailed explanation of what went wrong or what you
should change to get rid of a warning. See (TODO) for more information about errors, warnings and informational
messages.

If compilation happened without any problems, you should output similar to the following example:

```
$ ./mosc
Compilation succeeded (took 0.213 s overall, 0.155 s compilation)
```

[MCWiki/Data_pack]: https://minecraft.gamepedia.com/Data_pack
