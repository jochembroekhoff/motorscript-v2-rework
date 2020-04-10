package nl.jochembroekhoff.motorscript.common.util

import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result

object StringUtil {

    val NO_ESCAPE = Regex("[A-Za-z0-9_\\-+]+")

    fun unqote(input: String): String {
        if (input.isEmpty()) {
            return ""
        }

        if (input.length == 1) {
            if (input != "'" && input != "\"") {
                return input
            } else {
                throw IllegalArgumentException("Invalid input string.")
            }
        }

        if (input.startsWith('\'') && input.endsWith('\'') ||
            input.startsWith('\"') && input.endsWith('\"')
        ) {
            return input.substring(1, input.length - 1)
        }

        throw IllegalArgumentException("Invalid input string.")
    }

    fun unescape(input: String): Result<String, String> {
        if (input.matches(NO_ESCAPE)) {
            return Ok(input)
        }

        val chars = input.toCharArray()
        val res = CharArray(chars.size)
        var unescapedLength = 0

        var inEscape = false
        chars.forEach { c ->
            if (inEscape) {
                if (c == 't' || c == 'n' || c == '\\' || c == '"' || c == '\'') {
                    res[unescapedLength++] = when (c) {
                        't' -> '\t'
                        'n' -> '\n'
                        else -> c
                    }
                    inEscape = false
                } else {
                    return Error("Cannot escape char '$c'")
                }
            } else {
                if (c == '\\') {
                    inEscape = true
                } else {
                    res[unescapedLength++] = c
                }
            }
        }

        return Ok(String(res, 0, unescapedLength))
    }
}
