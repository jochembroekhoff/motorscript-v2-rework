package nl.jochembroekhoff.motorscript.common.messages

import java.io.File

open class SourceReferenceAttachment(val file: File, val start: SourcePosition, val end: SourcePosition?) : MessageAttachment {
    override fun toMessageString(): String {
        return if (end == null) {
            "${file}(${start.format()})"
        } else {
            "${file}(${start.format()}:${end.format()})"
        }
    }
}
