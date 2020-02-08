package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class ExpressionListVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) :
    MOSExtendedVisitor<List<IRExpressionVertex>>(ectx, g) {
    override fun visitExpressionList(ctx: MOSParser.ExpressionListContext): List<IRExpressionVertex> {
        return ctx.expression().map { ExpressionVisitor(ectx, g).visitExpression(it) }
    }
}
