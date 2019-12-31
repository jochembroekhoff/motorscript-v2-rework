package nl.jochembroekhoff.motorscript.common.messages

open class ThrowableAttachment(val throwable: Throwable) : MessageAttachment {
    override fun toMessageString(): String {
        return throwable.message ?: ""
    }
}
