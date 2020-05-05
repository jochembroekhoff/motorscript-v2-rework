package nl.jochembroekhoff.motorscript.gen.impl.expr

import nl.jochembroekhoff.motorscript.def.DefSignature
import nl.jochembroekhoff.motorscript.gen.GenContext
import nl.jochembroekhoff.motorscript.gen.api.FunctionGenerator
import nl.jochembroekhoff.motorscript.gen.api.GenValue
import nl.jochembroekhoff.motorscript.gen.api.ReturnState
import nl.jochembroekhoff.motorscript.gen.api.ReturnsVoid

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
