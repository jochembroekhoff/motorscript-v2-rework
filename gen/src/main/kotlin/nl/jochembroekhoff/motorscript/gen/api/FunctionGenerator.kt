package nl.jochembroekhoff.motorscript.gen.api

import nl.jochembroekhoff.motorscript.gen.GenContext

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
