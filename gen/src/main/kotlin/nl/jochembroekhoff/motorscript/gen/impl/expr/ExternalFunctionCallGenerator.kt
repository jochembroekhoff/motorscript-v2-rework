package nl.jochembroekhoff.motorscript.gen.impl.expr

import nl.jochembroekhoff.motorscript.def.DefSignature
import nl.jochembroekhoff.motorscript.gen.GenContext
import nl.jochembroekhoff.motorscript.gen.api.FunctionCallGenerator
import nl.jochembroekhoff.motorscript.gen.api.GenValue
import nl.jochembroekhoff.motorscript.gen.api.ReturnState
import nl.jochembroekhoff.motorscript.gen.api.ReturnsVoid

class ExternalFunctionCallGenerator(gctx: GenContext, private val signature: DefSignature) : FunctionCallGenerator(gctx) {
    override fun generate(
        positional: List<GenValue>,
        named: List<Pair<String, GenValue>>
    ): ReturnState {
        // TODO: Implement
        return ReturnsVoid()
    }
}
