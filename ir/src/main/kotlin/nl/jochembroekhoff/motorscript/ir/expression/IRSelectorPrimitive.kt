package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRSelectorPrimitive(val target: Target) : IRExpressionVertex() {
    enum class Target(val repr: String) {
        PLAYER_NEAREST("p"),
        PLAYER_RANDOM("r"),
        PLAYER_ALL("a"),
        ENTITY_ALL("e"),
        ENTITY_SENDER("s");

        companion object {
            val REVERSE_MAPPING = mapOf(
                PLAYER_NEAREST.repr to PLAYER_NEAREST,
                PLAYER_RANDOM.repr to PLAYER_RANDOM,
                PLAYER_ALL.repr to PLAYER_ALL,
                ENTITY_ALL.repr to ENTITY_ALL,
                ENTITY_SENDER.repr to ENTITY_SENDER
            )
        }
    }

    override fun contentDescription(): String {
        return "Target: $target (${target.repr})"
    }
}
