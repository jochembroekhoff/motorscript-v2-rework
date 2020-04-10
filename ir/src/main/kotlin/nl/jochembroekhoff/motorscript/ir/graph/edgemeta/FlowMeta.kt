package nl.jochembroekhoff.motorscript.ir.graph.edgemeta

data class FlowMeta(
    val pop: Boolean = false,
    val guard: Set<Guard> = setOf()
) : EdgeMeta() {
    override fun contentDescription(): String {
        return if (guard.isEmpty()) {
            if (pop) {
                "POP"
            } else {
                super.contentDescription()
            }
        } else {
            "${if (pop) "POP, " else ""}Guard: $guard"
        }
    }
}
