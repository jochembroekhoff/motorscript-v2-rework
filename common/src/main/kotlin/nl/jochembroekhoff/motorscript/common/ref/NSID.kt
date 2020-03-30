package nl.jochembroekhoff.motorscript.common.ref

data class NSID(val namespace: String, val name: List<String>) {
    fun toDebugString(): String {
        return "$namespace:${name.joinToString("\\")}"
    }

    operator fun div(nameAppend: String): NSID {
        return copy(name = name + nameAppend)
    }
}
