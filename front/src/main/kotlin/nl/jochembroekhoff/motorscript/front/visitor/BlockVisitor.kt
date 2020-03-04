package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class BlockVisitor(vctx: VisitorContext) : MOSExtendedVisitor<Pair<IRStatementVertex, IRStatementVertex>>(vctx) {
    override fun visitBlock(ctx: MOSParser.BlockContext): Pair<IRStatementVertex, IRStatementVertex> {
        val statements = ctx.statement()
        if (statements.isEmpty()) {
            throw InternalAssertionExecutionException("No statements, expected at least one")
        }

        var firstStatement: IRStatementVertex? = null
        var lastStatement: IRStatementVertex? = null
        statements.forEach { stmtCtx ->
            val statementVisitor = StatementVisitor(vctx)
            val statement = statementVisitor.visit(stmtCtx)
            lastStatement?.gFollowedBy(statement)
            lastStatement = statement
            if (firstStatement == null) firstStatement = statement
        }

        return Pair(firstStatement!!, lastStatement!!)
    }
}
