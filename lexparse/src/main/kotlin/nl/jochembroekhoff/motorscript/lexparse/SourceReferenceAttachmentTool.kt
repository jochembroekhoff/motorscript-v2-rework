package nl.jochembroekhoff.motorscript.lexparse

import nl.jochembroekhoff.motorscript.common.messages.SourcePosition
import nl.jochembroekhoff.motorscript.common.messages.SourceReferenceAttachment
import org.antlr.v4.runtime.Token
import java.nio.file.Path

object SourceReferenceAttachmentTool {
    fun fromTokenInFile(source: Path, token: Token): SourceReferenceAttachment {
        return SourceReferenceAttachment(source, SourcePosition(token.line, token.charPositionInLine))
    }
}
