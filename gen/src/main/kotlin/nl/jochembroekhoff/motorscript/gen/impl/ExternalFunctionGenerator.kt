package nl.jochembroekhoff.motorscript.gen.impl

import nl.jochembroekhoff.motorscript.def.DefSignature
import nl.jochembroekhoff.motorscript.gen.GenContext

class ExternalFunctionGenerator(private val signature: DefSignature) : FunctionGenerator() {
    override fun invoke(
        gctx: GenContext,
        positional: List<GenValue>,
        named: List<Pair<String, GenValue>>
    ): ReturnState {
        // TODO: Implement
        return ReturnsVoid()
    }
}
