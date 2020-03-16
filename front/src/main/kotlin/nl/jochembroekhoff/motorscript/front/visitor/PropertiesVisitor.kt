package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class PropertiesVisitor(vctx: VisitorContext) : MOSExtendedVisitor<List<Pair<String, IRExpressionVertex>>>(vctx) {
    override fun visitProperties(ctx: MOSParser.PropertiesContext): List<Pair<String, IRExpressionVertex>> {
        return ctx.property().map { propCtx ->
            val name = (propCtx.identifier()
                ?: throw FeatureUnimplementedExecutionException("Key-value properties only supported currently.")).text
            val exprV = ExpressionVisitor(vctxNext()).visitExpression(propCtx.expression())
            Pair(name, exprV)
        }
    }
}
