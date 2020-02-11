package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRExpressionStatement
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRIf
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRReturn
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class StatementVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) :
    MOSExtendedVisitor<IRStatementVertex>(ectx, g) {
    override fun visitDeclarationStatement(ctx: MOSParser.DeclarationStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Declaration statements are not implemented yet.")
    }

    override fun visitDeferStatement(ctx: MOSParser.DeferStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Defer statements are not implemented yet.")
    }

    override fun visitExecuteStatement(ctx: MOSParser.ExecuteStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Execute statements are not implemented yet.")
    }

    override fun visitForIn(ctx: MOSParser.ForInContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("For statements statements are not implemented yet.")
    }

    override fun visitIfStatement(ctx: MOSParser.IfStatementContext): IRIf {
        val ifV = gMkV { IRIf() }

        fun createBranch(
            exprCtx: MOSParser.ExpressionContext,
            blockCtx: MOSParser.BlockContext
        ): Pair<IRExpressionVertex, Pair<IRStatementVertex, IRStatementVertex>> {
            val conditionExpr = ExpressionVisitor(ectx, g).visitExpression(exprCtx)
            val block = BlockVisitor(ectx, g).visitBlock(blockCtx)
            return Pair(conditionExpr, block)
        }

        val branches = sequenceOf(ctx.ifMainBranch().let { createBranch(it.expression(), it.block()) }) +
            ctx.ifElseIfBranch().asSequence().map { createBranch(it.expression(), it.block()) }

        branches.forEach { (expr, branch) ->
            ifV.gDependOn(expr)
            ifV.gBranchTo(branch.first)
        }

        ctx.ifElseBranch()?.also { elseBranchCtx ->
            val elseBlock = BlockVisitor(ectx, g).visitBlock(elseBranchCtx.block())
            ifV.gBranchTo(elseBlock.first)
        }

        return ifV
    }

    override fun visitSwitchStatement(ctx: MOSParser.SwitchStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Switch statements are not implemented yet.")
    }

    override fun visitReturnStatement(ctx: MOSParser.ReturnStatementContext): IRStatementVertex {
        val exprCtx = ctx.expression() ?: null
        return if (exprCtx == null) {
            gMkV { IRReturn(IRReturn.Type.VOID) }
        } else {
            val exprVisitor = ExpressionVisitor(ectx, g)
            val exprV = exprVisitor.visitExpression(exprCtx)
            gMkV { IRReturn(IRReturn.Type.EXPR) }.also { it.gDependOn(exprV) }
        }
    }

    override fun visitYieldStatement(ctx: MOSParser.YieldStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Yield statements are not implemented yet.")
    }

    override fun visitExpressionStatement(ctx: MOSParser.ExpressionStatementContext): IRStatementVertex {
        val exprStmtV = gMkV { IRExpressionStatement() }
        val exprVisitor = ExpressionVisitor(ectx, g)
        val exprV = exprVisitor.visitExpression(ctx.expression())
        exprStmtV.gDependOn(exprV)
        return exprStmtV
    }
}
