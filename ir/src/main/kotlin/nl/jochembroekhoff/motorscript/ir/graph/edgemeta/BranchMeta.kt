package nl.jochembroekhoff.motorscript.ir.graph.edgemeta

data class BranchMeta(
    val index: Int = 0
) : EdgeMeta() {
    override fun contentDescription(): String {
        return "Index: $index"
    }
}
