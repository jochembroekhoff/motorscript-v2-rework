package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.extensions.collections.whenNotEmpty
import nl.jochembroekhoff.motorscript.ir.expression.IRFind
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralString
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import nl.jochembroekhoff.motorscript.lexparse.util.LPLiteralUtil
import org.jgrapht.Graph

class ExpressionVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) :
    MOSExtendedVisitor<IRExpressionVertex>(ectx, g) {
    /**
     * Expression base visitor. Handles all expression combination cases, e.g. postfix operators, conditional
     * combination and finding.
     */
    override fun visitExpression(ctx: MOSParser.ExpressionContext): IRExpressionVertex {
        fun getRootExpr() = ExpressionVisitor(ectx, g).visitExpression(ctx.expression().first())

        ctx.find().whenNotEmpty { findCtxs ->
            // Hold reference to last find part, starting with the root expression, because all the finds will be
            // created as a chain leading up to the root expression
            var curr = getRootExpr()

            findCtxs.forEach { findCtx ->
                findCtx.findIndex()?.also { findIndexCtx ->
                    val vFind = gMkV { IRFind(IRFind.Type.INDEX) }
                    val expressionVisitor = ExpressionVisitor(ectx, g)
                    val expr = expressionVisitor.visitExpression(findIndexCtx.expression())
                    vFind.gDependOn(expr)
                    curr = vFind
                }
                findCtx.findPath()?.also { findPathCtx ->
                    // Extract the path string to be found
                    var stringValue = ""
                    findPathCtx.identifier()?.also { stringValue = it.text }
                    findPathCtx.literalString()?.also {
                        stringValue = LPLiteralUtil.extractString(it).expect("invalid string literal")
                    }

                    // Build graph
                    val vFind = gMkV { IRFind(IRFind.Type.PATH) }
                    vFind.gDependOn(gMkV { IRLiteralString(stringValue) })
                    curr = vFind
                }
            }

            return curr
        }

        return visitChildren(ctx)
    }
}
