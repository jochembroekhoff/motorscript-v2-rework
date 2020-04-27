package nl.jochembroekhoff.motorscript.gen.impl

import nl.jochembroekhoff.motorscript.gen.GenContext
import nl.jochembroekhoff.motorscript.gen.Generator

/**
 * Base class for function generators.
 */
abstract class FunctionGenerator : Generator() {
    abstract fun invoke(
        gctx: GenContext,
        positional: List<GenValue>,
        named: List<Pair<String, GenValue>>
    ): ReturnState
}
