package nl.jochembroekhoff.motorscript.common.ref

data class NSID(val namespace: String, val name: List<String>) {
    fun toDebugString(): String {
        return "$namespace:${name.joinToString("\\")}"
    }
}
