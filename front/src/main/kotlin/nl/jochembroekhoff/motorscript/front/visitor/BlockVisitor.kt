package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.front.Messages
import nl.jochembroekhoff.motorscript.front.visitres.BlockVisitorResult
import nl.jochembroekhoff.motorscript.front.visitres.Exit
import nl.jochembroekhoff.motorscript.front.visitres.ReturnKnowledge
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRReturn
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.FlowMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Guard
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class BlockVisitor(vctx: VisitorContext) : MOSExtendedVisitor<BlockVisitorResult>(vctx) {
    override fun visitBlock(ctx: MOSParser.BlockContext): BlockVisitorResult {
        val statements = ctx.statement()
        if (statements.isEmpty()) {
            throw InternalAssertionExecutionException("No statements, expected at least one")
        }

        var firstStatement: IRStatementVertex? = null
        var lastStatement: IRStatementVertex? = null
        var previousExits: Set<Exit>? = null
        var willReturn = ReturnKnowledge.NEVER
        statements.forEach { stmtCtx ->
            if (lastStatement is IRReturn) {
                // TODO: Attach source reference
                vctx.ectx.execution.messagePipe.dispatch(Messages.unreachableCode.new("Statements in a block after a return statement are never reachable."))
                return@forEach
            }

            val statementVisitor = StatementVisitor(vctxNext())
            val statement = statementVisitor.visit(stmtCtx)
            if (firstStatement == null) firstStatement = statement

            if (statement is IRReturn) {
                willReturn = ReturnKnowledge.ALWAYS
            }

            // TODO: can set willReturn=ALWAYS, for example when all branches of an if statement also have
            //  willReturn==ALWAYS

            lastStatement?.gFollowedBy(statement)
            previousExits?.forEach { exit ->
                if (exit.willReturn == ReturnKnowledge.NEVER) {
                    exit.v.gFollowedBy(statement, FlowMeta(pop = true))
                } else {
                    if (willReturn != ReturnKnowledge.ALWAYS) {
                        willReturn = ReturnKnowledge.MAYBE
                    }
                    exit.v.gFollowedBy(statement, FlowMeta(pop = true, guard = setOf(Guard.RETURN)))
                }
            }
            previousExits = statementVisitor.alternativeExits

            lastStatement = statement
        }

        return BlockVisitorResult(firstStatement!!, previousExits!! + Exit(lastStatement!!, willReturn))
    }
}
