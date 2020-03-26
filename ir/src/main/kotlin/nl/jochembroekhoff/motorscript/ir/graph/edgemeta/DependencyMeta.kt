package nl.jochembroekhoff.motorscript.ir.graph.edgemeta

data class DependencyMeta(
    val slot: Slot? = null
) : EdgeMeta() {
    override fun contentDescription(): String {
        return if (slot == null) {
            super.contentDescription()
        } else {
            "Slot: ${slot.format()}"
        }
    }
}
