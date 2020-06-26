package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.common.execution.internalAssert
import nl.jochembroekhoff.motorscript.common.extensions.collections.whenNotEmpty
import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.front.Messages
import nl.jochembroekhoff.motorscript.ir.expression.*
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.DependencyMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Slot
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import nl.jochembroekhoff.motorscript.lexparse.util.LPLiteralUtil

class ExpressionVisitor(vctx: VisitorContext) : MOSExtendedVisitor<IRExpressionVertex>(vctx) {
    /**
     * Expression base visitor. Handles all expression combination cases, e.g. postfix operators, conditional
     * combination and finding.
     */
    override fun visitExpression(ctx: MOSParser.ExpressionContext): IRExpressionVertex {
        fun getExprV(i: Int = 0) = ExpressionVisitor(vctxNext()).visitExpression(ctx.expression()[i])

        // This 'flat' finding could just be replaced with recursive finding, will produce the same IR in the end
        ctx.find().whenNotEmpty { findCtxs ->
            // Hold reference to last find part, starting with the root expression, because all the finds will be
            // created as a chain leading up to the root expression
            var curr = getExprV()

            findCtxs.forEach { findCtx ->
                findCtx.findIndex()?.also { findIndexCtx ->
                    val vFind = gMkV { IRFind(IRFind.Type.INDEX) }
                    val expressionVisitor = ExpressionVisitor(vctxNext())
                    val expr = expressionVisitor.visitExpression(findIndexCtx.expression())
                    vFind.gDependOn(expr, DependencyMeta(slot = Slot(Slot.Category.FIND))) // Index value dependency
                    vFind.gDependOn(curr, DependencyMeta(slot = Slot(Slot.Category.SOURCE))) // Ensure parts chain
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
                    vFind.gDependOn(
                        gMkV { IRLiteralString(stringValue) }, // Path find text value dependency
                        DependencyMeta(slot = Slot(Slot.Category.FIND))
                    )
                    vFind.gDependOn(curr, DependencyMeta(slot = Slot(Slot.Category.SOURCE))) // Ensure parts chain
                    curr = vFind
                }
            }

            return curr
        }

        ctx.invocation()?.also { ivkCtx ->
            val ivkV = gMkV { IRInvoke() }
            val targetV = getExprV()
            ivkV.gDependOn(targetV, DependencyMeta(slot = Slot(Slot.Category.TARGET)))
            ivkCtx.arguments().also { argsCtx ->
                argsCtx.expressionList()?.also { exprListCtx ->
                    ExpressionListVisitor(vctxNext()).visitExpressionList(exprListCtx).forEachIndexed { i, posArgV ->
                        ivkV.gDependOn(posArgV, DependencyMeta(slot = Slot(Slot.Category.ARG_POSITIONAL, index = i)))
                    }
                }
                argsCtx.expressionListNamed()?.also { exprListNamedCtx ->
                    ExpressionListNamedVisitor(vctxNext()).visitExpressionListNamed(exprListNamedCtx)
                        .forEach { (name, argV) ->
                            ivkV.gDependOn(argV, DependencyMeta(slot = Slot(Slot.Category.ARG_NAMED, name = name)))
                        }
                }
            }
            return ivkV
        }

        ctx.postfix()?.also { postfixCtx ->
            val targetV = getExprV()
            postfixCtx.MinusDouble()?.also {
                return gMkV { IRPostfix(IRPostfix.Op.DECR) }.also {
                    it.gDependOn(targetV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
                }
            }
            postfixCtx.PlusDouble()?.also {
                return gMkV { IRPostfix(IRPostfix.Op.INCR) }.also {
                    it.gDependOn(targetV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
                }
            }
            internalAssert(false) { "Unexpected postfix operator: ${postfixCtx.text}" }
        }

        ctx.prefix()?.also { prefixCtx ->
            val targetV = getExprV()
            prefixCtx.Exclam()?.also {
                return gMkV { IRUnary(IRUnary.Op.NEGATE) }.also {
                    it.gDependOn(targetV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
                }
            }
            prefixCtx.MinusDouble()?.also {
                return gMkV { IRUnary(IRUnary.Op.PRE_DECR) }.also {
                    it.gDependOn(targetV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
                }
            }
            prefixCtx.PlusDouble()?.also {
                return gMkV { IRUnary(IRUnary.Op.PRE_INCR) }.also {
                    it.gDependOn(targetV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
                }
            }
            internalAssert(false) { "Unexpected prefix/unary operator: ${prefixCtx.text}" }
        }

        // TODO: infix ops (*, /, %, +, -, &&, ||)

        ctx.compare()?.also { compCtx ->
            val comparisionType = when {
                compCtx.KwIs() != null -> IRCompare.Type.IS
                compCtx.KwMatches() != null -> IRCompare.Type.MATCHES
                compCtx.LessThan() != null -> IRCompare.Type.LT
                compCtx.LessThanOrEqualTo() != null -> IRCompare.Type.LTE
                compCtx.GreaterThan() != null -> IRCompare.Type.GT
                compCtx.GreaterThanOrEqualTo() != null -> IRCompare.Type.GTE
                compCtx.EqualsDouble() != null -> IRCompare.Type.EQ
                else -> throw InternalAssertionExecutionException("Unexpected comparison operator: ${compCtx.text}")
            }
            val compV = gMkV { IRCompare(comparisionType) }
            val leftV = getExprV(0)
            val rightV = getExprV(1)
            compV.gDependOn(leftV, DependencyMeta(slot = Slot(Slot.Category.SOURCE, name = "L")))
            compV.gDependOn(rightV, DependencyMeta(slot = Slot(Slot.Category.SOURCE, name = "R")))
            return compV
        }

        ctx.DotDot()?.also {
            throw FeatureUnimplementedExecutionException("Ranges are not implemented yet.")
        }

        ctx.rangeAndLower()?.also { rangeAndLowerCtx ->
            throw FeatureUnimplementedExecutionException("Ranges are not implemented yet.")
        }

        return visitChildren(ctx)
    }

    override fun visitLiteral(ctx: MOSParser.LiteralContext): IRLiteral<*> {
        val literalVisitor = LiteralVisitor(vctxNext())
        return literalVisitor.visit(ctx)
    }

    override fun visitSelector(ctx: MOSParser.SelectorContext): IRSelectorPrimitive {
        val targetRepr = ctx.identifier().text
        val target = IRSelectorPrimitive.Target.REVERSE_MAPPING[targetRepr]
        val selectorV = if (target == null) {
            vctx.ectx.execution.messagePipe.dispatch(
                Messages.implicitSelectorName.new(
                    "The selector used a non-default target. It will therefore query all entities with the name ${targetRepr}."
                )
            )
            gMkV { IRSelectorPrimitive(IRSelectorPrimitive.Target.ENTITY_ALL) }.also {
                it.gDependOn(
                    gMkV { IRLiteralString(targetRepr) },
                    DependencyMeta(slot = Slot(Slot.Category.PROPERTY, name = "name"))
                )
            }
        } else {
            gMkV { IRSelectorPrimitive(target) }
        }

        ctx.properties()?.also { propsCtx ->
            val propsVisitor = PropertiesVisitor(vctxNext())
            propsVisitor.visitProperties(propsCtx).forEach { (key, propV) ->
                selectorV.gDependOn(propV, DependencyMeta(slot = Slot(Slot.Category.PROPERTY, name = key)))
            }
        }

        return selectorV
    }

    override fun visitResource(ctx: MOSParser.ResourceContext): IRResource {
        val resV = gMkV { IRResource() }
        val refVisitor = RefVisitor(vctxNext())
        val refV = refVisitor.visitRef(ctx.ref())
        resV.gDependOn(refV, DependencyMeta(slot = Slot(Slot.Category.SOURCE)))
        ctx.properties()?.also { propsCtx ->
            val propsVisitor = PropertiesVisitor(vctxNext())
            propsVisitor.visitProperties(propsCtx).forEach { (key, propV) ->
                resV.gDependOn(propV, DependencyMeta(slot = Slot(Slot.Category.PROPERTY, name = key)))
            }
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
        val refVisitor = RefVisitor(vctxNext())
        return refVisitor.visitRef(ctx)
    }

    override fun visitTypeEnumReference(ctx: MOSParser.TypeEnumReferenceContext): IRExpressionVertex {
        throw FeatureUnimplementedExecutionException("Enum type references are not implemented yet.")
    }
}
