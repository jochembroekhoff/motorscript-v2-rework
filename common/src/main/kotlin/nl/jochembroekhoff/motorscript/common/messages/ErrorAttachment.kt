package nl.jochembroekhoff.motorscript.common.messages

import nl.jochembroekhoff.motorscript.common.result.Error

open class ErrorAttachment(private val error: Error<*, *>) : MessageAttachment {
    override fun toMessageString(): String {
        return "error: ${error.value}"
    }
}
