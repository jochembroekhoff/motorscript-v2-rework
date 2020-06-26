package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.front.Messages
import nl.jochembroekhoff.motorscript.front.Modifier
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class ModifierVisitor(vctx: VisitorContext) : MOSExtendedVisitor<Set<Modifier>>(vctx) {
    override fun visitFunctionModifiers(ctx: MOSParser.FunctionModifiersContext?): Set<Modifier> {
        if (ctx == null) {
            return setOf()
        }
        val res: MutableSet<Modifier> = HashSet()
        ctx.functionModifier().mapNotNull { processFunctionModifier(it) }.forEach { modifier ->
            if (res.contains(modifier)) {
                vctx.ectx.execution.messagePipe.dispatch(Messages.duplicateModifier.new("The modifier ${modifier.repr} was specified more than once. Specifying a modifier multiple times has no effect."                    )                )
            } else {
                res.add(modifier)
            }
        }
        return res
    }

    override fun visitFunctionModifier(ctx: MOSParser.FunctionModifierContext): Set<Modifier> {
        val modifier = processFunctionModifier(ctx)
        return if (modifier == null) {
            setOf()
        } else {
            setOf(modifier)
        }
    }

    private fun processFunctionModifier(ctx: MOSParser.FunctionModifierContext): Modifier? {
        return when {
            ctx.KwConst() != null -> Modifier.CONST
            ctx.KwDefault() != null -> Modifier.DEFAULT
            ctx.KwIterator() != null -> Modifier.ITERATOR
            ctx.KwPrivate() != null -> Modifier.PRIVATE
            ctx.KwPublic() != null -> Modifier.PUBLIC
            ctx.KwUser() != null -> Modifier.USER
            else -> null
        }
    }
}
