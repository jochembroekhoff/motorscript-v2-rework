package nl.jochembroekhoff.motorscript.lexparse

import nl.jochembroekhoff.motorscript.common.messages.MessagePipe
import nl.jochembroekhoff.motorscript.common.messages.SourcePosition
import nl.jochembroekhoff.motorscript.common.messages.SourceReferenceAttachment
import nl.jochembroekhoff.motorscript.lexparse.util.SourceReferenceAttachmentUtil
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token
import java.nio.file.Path

class MessagePipeErrorListener(private val source: Path, private val messagePipe: MessagePipe) : BaseErrorListener() {

    var errorCount = 0
        private set

    override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?
    ) {
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)

        errorCount++

        val attachable = if (offendingSymbol is Token) {
            SourceReferenceAttachmentUtil.fromTokenInFile(source, offendingSymbol)
        } else {
            SourceReferenceAttachment(source, SourcePosition(line, charPositionInLine))
        }

        messagePipe.dispatch(Messages.syntaxError.new(msg, listOf(attachable)))
    }
}
