package nl.jochembroekhoff.motorscript.gen.api

import nl.jochembroekhoff.motorscript.gen.GenContext

/**
 * Base class for function generators.
 */
abstract class FunctionCallGenerator(gctx: GenContext) : Generator(gctx) {
    abstract fun generate(
        positional: List<GenValue>,
        named: List<Pair<String, GenValue>>
    ): ReturnState
}
