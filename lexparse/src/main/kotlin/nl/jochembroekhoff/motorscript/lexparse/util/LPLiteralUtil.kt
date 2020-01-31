package nl.jochembroekhoff.motorscript.lexparse.util

import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.common.util.StringUtil
import nl.jochembroekhoff.motorscript.lexparse.MOSParser

object LPLiteralUtil {
    fun extractString(ctx: MOSParser.LiteralStringContext): Result<String, String> {
        return StringUtil.unescape(StringUtil.unqote(ctx.text))
    }
}
