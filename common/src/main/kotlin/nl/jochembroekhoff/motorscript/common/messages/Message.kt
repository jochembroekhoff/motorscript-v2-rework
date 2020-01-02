package nl.jochembroekhoff.motorscript.common.messages

data class Message(val base: MessageBase, val explanation: String? = null, val attachments: List<MessageAttachment> = listOf()) {
    fun format(): String {
        val builder = StringBuilder()

        builder.append(base.level)
        builder.append(' ')
        builder.append(base.category)
        builder.append('-')
        builder.append(base.serial.toString().padStart(4, '0'))
        builder.append(": ")
        builder.append(base.description)

        if (explanation != null) {
            builder.append("\n\texplanation: ")
            builder.append(explanation)
        }

        attachments.forEach { attachment ->
            builder.append('\n')
            builder.append(attachment.toMessageString().prependIndent("\t"))
        }

        return builder.toString()
    }
}
