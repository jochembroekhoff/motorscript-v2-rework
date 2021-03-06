package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.util.StringUtil
import nl.jochembroekhoff.motorscript.front.FeatureUnimplementedExecutionException
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteral
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralBoolean
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralInteger
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralString
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

class LiteralVisitor(vctx: VisitorContext) : MOSExtendedVisitor<IRLiteral<*>>(vctx) {
    override fun visitLiteralString(ctx: MOSParser.LiteralStringContext): IRLiteralString {
        val stringValue = StringUtil.unescape(StringUtil.unqote(ctx.String().text)).expect("invalid string format")
        return gMkV { IRLiteralString(stringValue) }
    }

    override fun visitLiteralBoolean(ctx: MOSParser.LiteralBooleanContext): IRLiteralBoolean {
        return gMkV { IRLiteralBoolean(ctx.KwTrue() != null) }
    }

    override fun visitLiteralReal(ctx: MOSParser.LiteralRealContext): IRLiteral<*> {
        throw FeatureUnimplementedExecutionException("Real literals are not implemented yet.")
    }

    override fun visitLiteralInteger(ctx: MOSParser.LiteralIntegerContext): IRLiteralInteger {
        val intValue = Integer.parseInt(ctx.Integer().text)
        return gMkV { IRLiteralInteger(intValue) }
    }
}
