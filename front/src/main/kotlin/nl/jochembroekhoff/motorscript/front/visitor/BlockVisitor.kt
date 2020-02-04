package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class BlockVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) :
    MOSExtendedVisitor<Pair<IRStatementVertex, IRStatementVertex>>(ectx, g) {
    override fun visitBlock(ctx: MOSParser.BlockContext): Pair<IRStatementVertex, IRStatementVertex> {
        val statements = ctx.statement()
        if (statements.isEmpty()) {
            throw InternalAssertionExecutionException("No statements, expected at least one")
        }

        var firstStatement: IRStatementVertex? = null
        var lastStatement: IRStatementVertex? = null
        statements.forEach { stmtCtx ->
            val statementVisitor = StatementVisitor(ectx, g)
            val statement = statementVisitor.visit(stmtCtx)
            lastStatement?.gFollowedBy(statement)
            lastStatement = statement
            if (firstStatement == null) firstStatement = statement
        }

        return Pair(firstStatement!!, lastStatement!!)
    }
}
