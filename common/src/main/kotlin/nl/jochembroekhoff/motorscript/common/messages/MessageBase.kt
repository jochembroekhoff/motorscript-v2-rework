package nl.jochembroekhoff.motorscript.common.messages

data class MessageBase(val level: Level, val category: String, val serial: Int) {
    fun format(): String {
        return "$level $category-${serial.toString().padStart(4, '0')}"
    }
}
