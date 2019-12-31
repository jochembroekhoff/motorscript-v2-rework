package nl.jochembroekhoff.motorscript.common.messages

data class Message(val base: MessageBase, val description: String, val attachments: List<MessageAttachment> = listOf()) {
    fun format(): String {
        return "${base.format()}: $description [${attachments.size} attachments]"
    }
}
