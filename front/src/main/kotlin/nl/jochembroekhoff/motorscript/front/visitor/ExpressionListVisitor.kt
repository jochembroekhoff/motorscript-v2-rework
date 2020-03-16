package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class ExpressionListVisitor(vctx: VisitorContext) : MOSExtendedVisitor<List<IRExpressionVertex>>(vctx) {
    override fun visitExpressionList(ctx: MOSParser.ExpressionListContext): List<IRExpressionVertex> {
        return ctx.expression().map { ExpressionVisitor(vctxNext()).visitExpression(it) }
    }
}
