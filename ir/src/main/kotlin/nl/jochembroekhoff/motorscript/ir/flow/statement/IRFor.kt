package nl.jochembroekhoff.motorscript.ir.flow.statement

class IRFor(val type: Type) : IRStatementVertex() {
    enum class Type {
        INFINITE,
        IN,
        WHILE
    }
}
