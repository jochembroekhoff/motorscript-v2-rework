package nl.jochembroekhoff.motorscript.gen

import nl.jochembroekhoff.motorscript.def.EDefBundle

data class GenContext(
    val dispatcher: Dispatcher,
    val currentOutput: GenOutput,
    val eDefBundle: EDefBundle
)
