package nl.jochembroekhoff.motorscript.lexparse.util

import nl.jochembroekhoff.motorscript.common.messages.SourcePosition
import nl.jochembroekhoff.motorscript.common.messages.SourceReferenceAttachment
import org.antlr.v4.runtime.Token
import java.nio.file.Path

object SourceReferenceAttachmentUtil {

    fun extractSourcePositionFromToken(token: Token): SourcePosition {
        val startIndex = token.startIndex
        val stopIndex = token.stopIndex
        if (startIndex == -1 || stopIndex == -1) {
            val length = stopIndex - startIndex + 1
            if (length > 1) {
                // Could maybe return with ending char
            }
        }
        return SourcePosition(token.line, token.charPositionInLine)
    }

    fun fromTokenInFile(source: Path, token: Token): SourceReferenceAttachment {
        return SourceReferenceAttachment(source, extractSourcePositionFromToken(token))
    }
}
