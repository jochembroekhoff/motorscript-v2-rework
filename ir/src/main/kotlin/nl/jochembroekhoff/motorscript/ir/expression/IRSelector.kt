package nl.jochembroekhoff.motorscript.ir.expression

class IRSelector(val target: Target) {
    enum class Target(val repr: String) {
        PLAYER_NEAREST("p"),
        PLAYER_RANDOM("r"),
        PLAYER_ALL("a"),
        ENTITY_ALL("e"),
        ENTITY_SENDER("s")
    }
}
