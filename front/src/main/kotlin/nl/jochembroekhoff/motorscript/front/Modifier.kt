package nl.jochembroekhoff.motorscript.front

enum class Modifier(val repr: String, val forFunction: Boolean, val forContainer: Boolean) {
    BUILTIN("builtin", true, true),
    CONST("const", true, false),
    DEFAULT("default", true, true),
    ITERATOR("iterator", true, false),
    PRIVATE("private", true, true),
    PUBLIC("public", true, true),
    USER("user", true, false),
}
