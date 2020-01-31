package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.expression.IRFind
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralString
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class ExpressionVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) :
    MOSExtendedVisitor<IRExpressionVertex>(ectx, g) {
    /**
     * Expression base visitor. Handles all expression combination cases, e.g. postfix operators, conditional
     * combination and finding.
     */
    override fun visitExpression(ctx: MOSParser.ExpressionContext): IRExpressionVertex {
        fun getRootExpr() = ExpressionVisitor(ectx, g).visitExpression(ctx.expression(0))

        ctx.find()?.also { findCtxs ->
            val rootExpr = getRootExpr()

            // LEFTOFF: chain all find parts starting from rootExpr, return outer most find vertex

            findCtxs.forEach { findCtx ->
                findCtx.findIndex()?.also { findIndexCtx ->
                    val vFind = gMkV { IRFind(IRFind.Type.INDEX) }
                    val expressionVisitor = ExpressionVisitor(ectx, g)
                    val expr = expressionVisitor.visitExpression(findIndexCtx.expression())
                    vFind.gDependOn(expr)
                }
                findCtx.findPath()?.also path@{ findPathCtx ->
                    // LEFTOFF: extract string value
                    val vFind = gMkV { IRFind(IRFind.Type.PATH) }
                    val pathLiteral = gMkV { IRLiteralString("TODO") }
                    vFind.gDependOn(pathLiteral)
                }
            }
        }

        // TODO: Null check?
        return visitChildren(ctx)
    }
}
