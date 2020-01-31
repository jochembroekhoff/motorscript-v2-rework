package nl.jochembroekhoff.motorscript.common.messages

data class MessageBase(val level: Level, val category: String, val serial: Int, val description: String) {
    fun new(description: String? = null, attachables: List<Attachable> = listOf()): Message {
        return Message(this, description, attachables)
    }
}
