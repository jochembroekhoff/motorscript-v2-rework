package nl.jochembroekhoff.motorscript.ir.graph.edgemeta

data class Slot(val category: Category, val name: String = "", val index: Int = 0) {
    enum class Category {
        PROPERTY,
        ARG_POSITIONAL,
        AGR_NAMED,
        SOURCE,
        TARGET,
    }
}
