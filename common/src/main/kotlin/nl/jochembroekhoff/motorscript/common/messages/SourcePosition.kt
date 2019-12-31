package nl.jochembroekhoff.motorscript.common.messages

data class SourcePosition(val line: Int, val char: Int) {
    fun format(): String {
        return "${line}:${char}"
    }
}
