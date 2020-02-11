package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.extensions.collections.whenNotEmpty
import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.ir.expression.*
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import nl.jochembroekhoff.motorscript.lexparse.util.LPLiteralUtil
import org.jgrapht.Graph

class ExpressionVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) :
    MOSExtendedVisitor<IRExpressionVertex>(ectx, g) {
    /**
     * Expression base visitor. Handles all expression combination cases, e.g. postfix operators, conditional
     * combination and finding.
     */
    override fun visitExpression(ctx: MOSParser.ExpressionContext): IRExpressionVertex {
        fun getRootExpr() = ExpressionVisitor(ectx, g).visitExpression(ctx.expression().first())

        ctx.find().whenNotEmpty { findCtxs ->
            // Hold reference to last find part, starting with the root expression, because all the finds will be
            // created as a chain leading up to the root expression
            var curr = getRootExpr()

            findCtxs.forEach { findCtx ->
                findCtx.findIndex()?.also { findIndexCtx ->
                    val vFind = gMkV { IRFind(IRFind.Type.INDEX) }
                    val expressionVisitor = ExpressionVisitor(ectx, g)
                    val expr = expressionVisitor.visitExpression(findIndexCtx.expression())
                    vFind.gDependOn(expr)
                    curr = vFind
                }
                findCtx.findPath()?.also { findPathCtx ->
                    // Extract the path string to be found
                    var stringValue = ""
                    findPathCtx.identifier()?.also { stringValue = it.text }
                    findPathCtx.literalString()?.also {
                        stringValue = LPLiteralUtil.extractString(it).expect("invalid string literal")
                    }

                    // Build graph
                    val vFind = gMkV { IRFind(IRFind.Type.PATH) }
                    vFind.gDependOn(gMkV { IRLiteralString(stringValue) })
                    curr = vFind
                }
            }

            return curr
        }

        ctx.invocation()?.also { ivkCtx ->
            val ivkV = gMkV { IRInvoke() }
            val targetV = getRootExpr()
            ivkV.gDependOn(targetV)
            ivkCtx.arguments().also { argsCtx ->
                argsCtx.expressionList()?.also { exprListCtx ->
                    ExpressionListVisitor(ectx, g).visitExpressionList(exprListCtx).forEach {
                        ivkV.gDependOn(it)
                    }
                }
                argsCtx.expressionListNamed()?.also { exprListNamedCtx ->
                    ExpressionListNamedVisitor(ectx, g).visitExpressionListNamed(exprListNamedCtx)
                        .forEach { (_, argV) ->
                            // TODO: Set name in dependency edge
                            ivkV.gDependOn(argV)
                        }
                }
            }
            return ivkV
        }

        ctx.position()?.also { postfixCtx ->
            throw FeatureUnimplementedExecutionException("Postfix operations are not implemented yet.")
        }

        ctx.prefix()?.also { prefixCtx ->
            val targetV = getRootExpr()
            prefixCtx.Exclam()?.also {
                return gMkV { IRUnary(IRUnary.Op.NEGATE) }.also {
                    it.gDependOn(targetV)
                }
            }
            prefixCtx.MinusDouble()?.also {
                return gMkV { IRUnary(IRUnary.Op.PRE_DECR) }.also {
                    it.gDependOn(targetV)
                }
            }
            prefixCtx.PlusDouble()?.also {
                return gMkV { IRUnary(IRUnary.Op.PRE_INCR) }.also {
                    it.gDependOn(targetV)
                }
            }
            internalAssert(false, "Unexpexted prefix/unary operator")
        }

        ctx.compare()?.also { compCtx ->
            throw FeatureUnimplementedExecutionException("Comparision operations are not implemented yet.")
        }

        ctx.DotDot()?.also {
            throw FeatureUnimplementedExecutionException("Ranges are not implemented yet.")
        }

        ctx.rangeAndLower()?.also { rangeAndLowerCtx ->
            throw FeatureUnimplementedExecutionException("Ranges are not implemented yet.")
        }

        ctx.assign()?.also { assignCtx ->
            throw FeatureUnimplementedExecutionException("Assignment is not implemented yet.")
        }

        return visitChildren(ctx)
    }

    override fun visitLiteral(ctx: MOSParser.LiteralContext): IRLiteral<*> {
        val literalVisitor = LiteralVisitor(ectx, g)
        return literalVisitor.visit(ctx)
    }

    override fun visitSelector(ctx: MOSParser.SelectorContext): IRSelectorPrimitive {
        val targetRepr = ctx.identifier().text
        val target = IRSelectorPrimitive.Target.REVERSE_MAPPING[targetRepr]
        val selectorV = if (target == null) {
            gMkV { IRSelectorPrimitive(IRSelectorPrimitive.Target.ENTITY_ALL) }.also {
                // TODO: Dispatch info message informing about the fact that a selector was implicitly converted
                // TODO: Add first dependency to the "name" slot with the value being targetRepr
            }
        } else {
            gMkV { IRSelectorPrimitive(target) }
        }

        ctx.properties()?.also { propsCtx ->
            val propsVisitor = PropertiesVisitor(ectx, g)
            val props = propsVisitor.visitProperties(propsCtx)
            // TODO: Attach props
        }

        return selectorV
    }

    override fun visitResource(ctx: MOSParser.ResourceContext): IRResource {
        val resV = gMkV { IRResource() }
        val refVisitor = RefVisitor(ectx, g)
        val refV = refVisitor.visitRef(ctx.ref())
        resV.gDependOn(refV)
        ctx.properties()?.also { propsCtx ->
            val propsVisitor = PropertiesVisitor(ectx, g)
            val props = propsVisitor.visitProperties(propsCtx)
            // TODO: Attach props
        }
        return resV
    }

    override fun visitTag(ctx: MOSParser.TagContext): IRExpressionVertex {
        throw FeatureUnimplementedExecutionException("Tags are not implemented yet.")
    }

    override fun visitNbt(ctx: MOSParser.NbtContext): IRExpressionVertex {
        throw FeatureUnimplementedExecutionException("NBT expressions are not implemented yet.")
    }

    override fun visitPosition(ctx: MOSParser.PositionContext): IRExpressionVertex {
        throw FeatureUnimplementedExecutionException("Positions are not implemented yet.")
    }

    override fun visitVector(ctx: MOSParser.VectorContext): IRExpressionVertex {
        throw FeatureUnimplementedExecutionException("Vectors are not implemented yet.")
    }

    override fun visitRef(ctx: MOSParser.RefContext): IRRef {
        val refVisitor = RefVisitor(ectx, g)
        return refVisitor.visitRef(ctx)
    }

    override fun visitTypeEnumReference(ctx: MOSParser.TypeEnumReferenceContext): IRExpressionVertex {
        throw FeatureUnimplementedExecutionException("Enum type references are not implemented yet.")
    }
}
