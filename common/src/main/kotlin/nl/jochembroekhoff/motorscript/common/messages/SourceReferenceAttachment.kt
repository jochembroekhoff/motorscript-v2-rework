package nl.jochembroekhoff.motorscript.common.messages

import java.nio.file.Path

open class SourceReferenceAttachment(val file: Path, val start: SourcePosition, val end: SourcePosition? = null) : MessageAttachment {
    override fun toMessageString(): String {
        return if (end == null) {
            "source reference: ${file}(${start.format()})"
        } else {
            "source reference: ${file}(${start.format()}:${end.format()})"
        }
    }
}
