See the [Wikipedia page](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) for more in-depth details and background
information about the Game of Life.

# Rules

The rules for Life are as follows (condensed variant):

1. Any live cell with two or three neighbors survives.
2. Any dead cell with three live neighbors becomes a live cell.
3. All other live cells die in the next generation. Similarly, all other dead cells stay dead.

# Creating the world

Before we're going to do any coding, it's necessary to prepare a world which we'll use to see Life in action.
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

...
