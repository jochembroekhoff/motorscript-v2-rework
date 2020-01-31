package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.flow.misc.IREntry
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRReturn
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class FunctionVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) : MOSExtendedVisitor<IREntry>(ectx, g) {
    override fun visitFunctionBody(ctx: MOSParser.FunctionBodyContext): IREntry {
        val entryPoint = gMkV { IREntry() }

        ctx.expressionStatement()?.also { exprStmtCtx ->
            val retStmt = gMkV { IRReturn() }
            g.addEdge(entryPoint, retStmt)
            val exprVisitor = ExpressionVisitor(ectx, g)
            val expr = exprVisitor.visitExpression(exprStmtCtx.expression())
            retStmt.gDependOn(expr)
        }

        ctx.block()?.also { blockCtx ->
            val blockVisitor = BlockVisitor(ectx, g)
            val (blockEntry, blockExit) = blockVisitor.visitBlock(blockCtx)
            entryPoint.gFollowedBy(blockEntry)
        }

        return entryPoint
    }
}
