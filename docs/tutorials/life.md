# About

This is a moderately difficult tutorial, meant for people who already have some prior coding experience with plain
Minecraft commands and creating data packs who want to get into MotorScript quickly and explore many of its features.

See the [Wikipedia page](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) for more in-depth details and background
information about the Game of Life (or simply "Life").

The rules for Life are as follows (condensed variant):

1. Any live cell with two or three neighbors survives.
2. Any dead cell with three live neighbors becomes a live cell.
3. All other live cells die in the next generation. Similarly, all other dead cells stay dead.

# Creating the world

Before we can start developing, it's necessary to prepare a world which we'll use to see Life in action.
It's up to you which kind of world you want to use, but I prefer a simple void world.

After you've created a fresh new world, it's time to decide where the game's board is going to be located.
If you're using a void world like me, it's very easy to use a plane from the origin of the world (0, 0, 0) position.

It's not really necessary to create the game board right now, we'll make sure the game board is reset every time you
want to play. But, it might be useful to visualize ahead of time how the board will look like.

To create my game board, I issued the following command to fill the platform:

```
/fill 0 0 0 50 50 50 white_concrete
```

Note that I filled the board with white concrete. That's just an arbitrary block I've decided to use as the base for the
game board. You have to decide which blocks you want to use too. Since every cell, as we know from the rules, is either
dead or alive, there are two possible cell states. And these two cell states will correspond to two different physica
blocks. To make it easier on your eyes, try to choose blocks that contrast well. I like to use white concrete blocks for
cells that are dead and black concrete blocks for cells that are dead. And that's just because that is a conventional
color combination for simulations of the Game of Life.

# Project structure

To get started coding, the basic project structure has to be created. See the [introduction](../introduction.md) for
what this will look like.

For this tutorial the directory structure should look like this:

```
mosbuild.json
src/
    life/
        mos/
            advance.mos
            logic.mos
```

As you can see, we will end up using only two source files of MotorScript code, called `advance.mos` and `logic.mos`.
You don't necessarily have to create the `advance.mos` file now immediately, since we'll start working on `logic.mos`
first. If a source file is empty, MotorScript regards it as being invalid, and the compilation will not succeed.

# Build configuration

Before diving into the code, it's necessary to configure the build, basically instructing the compiler what you want to
be done and how the project should be treated. See [Build spec](../configuration/build-spec.md) for extensive details
about the build configuration.

The content of `mosbuild.json` should be as follows:

```json
{
  "name": "life",
  "version": "1.0.0",
  "description": "MotorScript tutorial: Game of Life",
  "targets": [{
    "platform": "java",
    "version": "1.16"
  }]
}
```

The `name` field here contains the value `life`, since that's what we used as the namespace for the source files too,
but that does not strictly have to be the same. Theoretically it can be any string, but it's a good practice to keep it
the same.
Anyhow, it is meant to be a unique identifier for your project. If you were to make something for real, you should take
some time to consider this name properly, since it's not practical to change it in a later stage.

The `version` is just `1.0.0` for now. Could also be something `0.1.0` if like. For this tutorial project it doesn't
really matter. Usage of [Semantic Versioning](https://semver.org/) is highly recommended (any might be enforced in the
future).

What `description` you give is what will end up in the game data pack menu, because it will be copied to the final data
pack.

The content of `targets` is an array that lists to which platforms and which version of that platform you want to
compile the source code. Currently, only `java` as the platform and `1.16` as its version is supported.

There is also a `dependencies` field that you can specify in this `mosbuild.json` file, but we don't need that now.

# Planning the code

TODO

# Writing the code

TODO
