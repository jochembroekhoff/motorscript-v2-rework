package nl.jochembroekhoff.motorscript.front.visitor

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.ir.expression.IRFullRef
import nl.jochembroekhoff.motorscript.ir.expression.IRPartialRef
import nl.jochembroekhoff.motorscript.ir.expression.IRRef
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class RefVisitor(vctx: VisitorContext) : MOSExtendedVisitor<IRRef>(vctx) {
    companion object : KLogging()

    override fun visitRef(ctx: MOSParser.RefContext): IRRef {
        val path = extractPath(ctx.refName())

        return if (ctx.Colon() == null) {
            gMkV { IRPartialRef(path, vctx.rctx /* TODO: figure out correct treatments */, setOf()) }
        } else {
            val namespace = ctx.refNamespace()?.identifier()?.text ?: vctx.rctx.localReferenceBase.namespace
            gMkV { IRFullRef(NSID(namespace, path)) }
        }
    }

    private fun extractPath(ctx: MOSParser.RefNameContext): List<String> {
        return ctx.identifier().map { it.text }
    }
}
