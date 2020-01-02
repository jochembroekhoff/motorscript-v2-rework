package nl.jochembroekhoff.motorscript.common.messages

data class MessageBase(val level: Level, val category: String, val serial: Int, val description: String) {
    fun new(description: String? = null, attachments: List<MessageAttachment> = listOf()): Message {
        return Message(this, description, attachments)
    }
}
