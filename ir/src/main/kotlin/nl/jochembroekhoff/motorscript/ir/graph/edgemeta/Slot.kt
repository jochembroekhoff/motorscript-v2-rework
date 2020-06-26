package nl.jochembroekhoff.motorscript.ir.graph.edgemeta

data class Slot(val category: Category, val name: String = "", val index: Int = 0) {
    enum class Category {
        PROPERTY,
        ARG_POSITIONAL,
        ARG_NAMED,
        SOURCE,
        TARGET,
        FIND,
    }

    init {
        if (index != 0 && name.isNotBlank()) {
            throw IllegalArgumentException("Use either name or index, or neither")
        }
    }

    fun format(): String {
        return "$category ($name#$index)"
    }
}
