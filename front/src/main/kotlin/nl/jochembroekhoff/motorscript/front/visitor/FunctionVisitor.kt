package nl.jochembroekhoff.motorscript.front.visitor

import mu.KLogging
import nl.jochembroekhoff.motorscript.front.visitres.ReturnKnowledge
import nl.jochembroekhoff.motorscript.ir.flow.misc.IREntry
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRReturn
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.DependencyMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Slot
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class FunctionVisitor(vctx: VisitorContext) : MOSExtendedVisitor<IREntry>(vctx) {

    companion object : KLogging()

    override fun visitFunctionBody(ctx: MOSParser.FunctionBodyContext): IREntry {
        val entryPoint = gMkV { IREntry() }

        ctx.expressionStatement()?.also { exprStmtCtx ->
            val retStmt = gMkV { IRReturn(IRReturn.Type.EXPR) }
            val exprVisitor = ExpressionVisitor(vctxNext())
            val expr = exprVisitor.visitExpression(exprStmtCtx.expression())
            retStmt.gDependOn(expr, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
            entryPoint.gFollowedBy(retStmt)
        }

        ctx.block()?.also { blockCtx ->
            val blockVisitor = BlockVisitor(vctxNext())
            val block = blockVisitor.visitBlock(blockCtx)
            entryPoint.gFollowedBy(block.entry)

            // Add synthetic return vertex and FLOW edges to it if necessary
            val syntheticRetNeedingExits = block.exits.filter { it.willReturn != ReturnKnowledge.ALWAYS }
            if (syntheticRetNeedingExits.isNotEmpty()) {
                logger.trace { "Function needs a synthetic return vertex for ${syntheticRetNeedingExits.size} exits" }
                val surrogateRet = gMkV(synthetic = true) { IRReturn(IRReturn.Type.VOID) }
                syntheticRetNeedingExits.forEach { it.v.gFollowedBy(surrogateRet) }
            }
        }

        return entryPoint
    }
}
