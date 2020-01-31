package nl.jochembroekhoff.motorscript.common.messages

interface MessageAttachment : Attachable {
    fun toMessageString(): String
    override fun toAttachment(): MessageAttachment = this
}
