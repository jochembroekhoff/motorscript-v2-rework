package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.internalAssert
import nl.jochembroekhoff.motorscript.front.Messages
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

/**
 * See function [visitExpressionListNamed] for more details.
 */
class ExpressionListNamedVisitor(vctx: VisitorContext) : MOSExtendedVisitor<Map<String, IRExpressionVertex>>(vctx) {
    /**
     * Visit a named expression list. When a duplicate is found (i.e. when there are multiple arguments specified for
     * the same parameter), an error message will be dispatched to the current message pipe.
     * @return A [Map] with names mapped to expression vertices. Iteration order is guaranteed to be identical to the
     * order in which the (name, expression)-pairs were parsed (because a [LinkedHashMap] is used internally).
     */
    override fun visitExpressionListNamed(ctx: MOSParser.ExpressionListNamedContext): Map<String, IRExpressionVertex> {
        val identifierCtxs = ctx.identifier()
        val exprCtxs = ctx.expression()
        internalAssert(identifierCtxs.size == exprCtxs.size) {
            "Expected identifiers and expressions to be of equivalent of length"
        }

        val seenNames: MutableSet<String> = HashSet()
        val duplicates: MutableSet<String> = HashSet()
        val res: MutableMap<String, IRExpressionVertex> = LinkedHashMap()

        val exprCtxsIter = exprCtxs.iterator()
        identifierCtxs.forEach { identifierCtx ->
            val exprCtx = exprCtxsIter.next()
            val name = identifierCtx.text
            if (name in seenNames) {
                if (name !in duplicates) {
                    duplicates += name
                    vctx.ectx.execution.messagePipe.dispatch(Messages.duplicateNamedArgument.new("Argument for parameter $name specified more than once."))
                }
                return@forEach
            }
            seenNames += name
            val expr = ExpressionVisitor(vctxNext()).visitExpression(exprCtx)
            res[name] = expr
        }

        return res
    }
}
