package nl.jochembroekhoff.motorscript.gen

import nl.jochembroekhoff.motorscript.pluginapi.registration.Registry

data class GenContext(
    val dispatcher: Dispatcher,
    val currentOutput: GenOutput,
    val registry: Registry
)
