package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.ir.expression.IRPartialRef
import nl.jochembroekhoff.motorscript.ir.flow.statement.*
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.DependencyMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Slot
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class StatementVisitor(vctx: VisitorContext) : MOSExtendedVisitor<IRStatementVertex>(vctx) {
    override fun visitDeclarationStatement(ctx: MOSParser.DeclarationStatementContext): IRStatementVertex {
        // TODO: process modifiers, enforced type & all stuff
        // TODO: increment ref scope

        val declTargetCtx = ctx.declarationTarget()
        if (declTargetCtx.vector() != null) {
            // In case of vector declaration, all vector elements should be plain identifiers
            throw FeatureUnimplementedExecutionException("Vector declaration statements are not implemented yet.")
        }

        return gMkV { IRAssign() }.also {
            it.gDependOn(
                gMkV { IRPartialRef(listOf(declTargetCtx.identifier().text), vctx.rctx) },
                DependencyMeta(slot = Slot(Slot.Category.TARGET))
            )
            it.gDependOn(
                ExpressionVisitor(vctxNext()).visitExpression(ctx.expression()),
                DependencyMeta(slot = Slot(Slot.Category.SOURCE))
            )
        }
    }

    override fun visitDeferStatement(ctx: MOSParser.DeferStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Defer statements are not implemented yet.")
    }

    override fun visitExecuteStatement(ctx: MOSParser.ExecuteStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Execute statements are not implemented yet.")
    }

    override fun visitForStatement(ctx: MOSParser.ForStatementContext): IRStatementVertex {
        ctx.forInfinite()?.also { forInfCtx ->
            val forV = gMkV { IRFor(IRFor.Type.INFINITE) }
            val block = BlockVisitor(vctxNext()).visitBlock(forInfCtx.block())
            forV.gBranchTo(block.first)
            block.second.gFollowedBy(forV)
            return forV
        }

        ctx.forIn()?.also { firInCtx ->
            throw FeatureUnimplementedExecutionException("For-in loops are not implemented yet.")
        }

        ctx.forWhile()?.also { forWhileCtx ->
            val conditionV = ExpressionVisitor(vctxNext()).visitExpression(forWhileCtx.expression())
            val forV = gMkV { IRFor(IRFor.Type.WHILE) }
            forV.gDependOn(conditionV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
            val block = BlockVisitor(vctxNext()).visitBlock(forWhileCtx.block())
            forV.gBranchTo(block.first)
            block.second.gFollowedBy(forV)
            return forV
        }

        throw InternalAssertionExecutionException("Unreachable.")
    }

    override fun visitIfStatement(ctx: MOSParser.IfStatementContext): IRIf {
        val ifStmtV = gMkV { IRIf() }

        fun createBranch(
            exprCtx: MOSParser.ExpressionContext,
            blockCtx: MOSParser.BlockContext
        ): Pair<IRExpressionVertex, Pair<IRStatementVertex, IRStatementVertex>> {
            val conditionExpr = ExpressionVisitor(vctxNext()).visitExpression(exprCtx)
            val block = BlockVisitor(vctxNext()).visitBlock(blockCtx)
            return Pair(conditionExpr, block)
        }

        val branches = sequenceOf(ctx.ifMainBranch().let { createBranch(it.expression(), it.block()) }) +
            ctx.ifElseIfBranch().asSequence().map { createBranch(it.expression(), it.block()) }

        branches.forEachIndexed { i, (expr, branch) ->
            ifStmtV.gDependOn(expr, DependencyMeta(slot = Slot(Slot.Category.SOURCE, index = i)))
            ifStmtV.gBranchTo(branch.first)
        }

        ctx.ifElseBranch()?.also { elseBranchCtx ->
            val elseBlock = BlockVisitor(vctxNext()).visitBlock(elseBranchCtx.block())
            ifStmtV.gBranchTo(elseBlock.first)
        }

        return ifStmtV
    }

    override fun visitSwitchStatement(ctx: MOSParser.SwitchStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Switch statements are not implemented yet.")
    }

    override fun visitReturnStatement(ctx: MOSParser.ReturnStatementContext): IRStatementVertex {
        val exprCtx = ctx.expression() ?: null
        return if (exprCtx == null) {
            gMkV { IRReturn(IRReturn.Type.VOID) }
        } else {
            val exprVisitor = ExpressionVisitor(vctxNext())
            val exprV = exprVisitor.visitExpression(exprCtx)
            gMkV { IRReturn(IRReturn.Type.EXPR) }.also {
                it.gDependOn(
                    exprV,
                    DependencyMeta(slot = Slot(Slot.Category.SOURCE))
                )
            }
        }
    }

    override fun visitYieldStatement(ctx: MOSParser.YieldStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Yield statements are not implemented yet.")
    }

    override fun visitAssignStatement(ctx: MOSParser.AssignStatementContext): IRStatementVertex {
        throw FeatureUnimplementedExecutionException("Assign statements are not implemented yet.")
    }

    override fun visitExpressionStatement(ctx: MOSParser.ExpressionStatementContext): IRStatementVertex {
        val exprStmtV = gMkV { IRExpressionStatement() }
        val exprVisitor = ExpressionVisitor(vctxNext())
        val exprV = exprVisitor.visitExpression(ctx.expression())
        exprStmtV.gDependOn(exprV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
        return exprStmtV
    }
}
