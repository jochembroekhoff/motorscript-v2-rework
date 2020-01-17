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
        val entryPoint = IREntry()
        g.addVertex(entryPoint)

        ctx.expressionStatement()?.also { exprStmtCtx ->
            val retStmt = IRReturn()
            g.addVertex(retStmt)
            // TODO: Add edge between retStmt and entryPoint, but which direction?
        }

        ctx.block()?.also { blockCtx ->
            // TODO: Process block
        }

        return entryPoint
    }
}
