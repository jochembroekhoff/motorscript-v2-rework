package nl.jochembroekhoff.motorscript.front.visitor

import mu.KLogging
import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.ir.expression.IRRef
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class RefVisitor(vctx: VisitorContext) : MOSExtendedVisitor<IRRef>(vctx) {
    companion object : KLogging()

    override fun visitRef(ctx: MOSParser.RefContext): IRRef {
        if (ctx.Colon() == null) {
            throw FeatureUnimplementedExecutionException("Local references not implemented yet.")
        } else {
            return gMkV { IRRef("LEFTOFF", vctx.rctx) }
        }
    }
}
