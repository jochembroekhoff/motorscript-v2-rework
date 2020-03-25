package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.ir.flow.misc.IREntry
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRReturn
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class FunctionVisitor(vctx: VisitorContext) : MOSExtendedVisitor<IREntry>(vctx) {
    override fun visitFunctionBody(ctx: MOSParser.FunctionBodyContext): IREntry {
        val entryPoint = gMkV { IREntry() }

        ctx.expressionStatement()?.also { exprStmtCtx ->
            val retStmt = gMkV { IRReturn(IRReturn.Type.EXPR) }
            val exprVisitor = ExpressionVisitor(vctxNext())
            val expr = exprVisitor.visitExpression(exprStmtCtx.expression())
            retStmt.gDependOn(expr)
            entryPoint.gFollowedBy(retStmt)
        }

        ctx.block()?.also { blockCtx ->
            val blockVisitor = BlockVisitor(vctxNext())
            val (blockEntry, blockExit) = blockVisitor.visitBlock(blockCtx)
            entryPoint.gFollowedBy(blockEntry)
        }

        return entryPoint
    }
}
