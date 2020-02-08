package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class PropertiesVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) :
    MOSExtendedVisitor<List<Pair<String, IRExpressionVertex>>>(ectx, g) {
    override fun visitProperties(ctx: MOSParser.PropertiesContext): List<Pair<String, IRExpressionVertex>> {
        return ctx.property().map { propCtx ->
            val name = (propCtx.identifier() ?: throw FeatureUnimplementedExecutionException("Key-value properties only supported currently.")).text
            val exprV = ExpressionVisitor(ectx, g).visitExpression(propCtx.expression())
            Pair(name, exprV)
        }
    }
}
