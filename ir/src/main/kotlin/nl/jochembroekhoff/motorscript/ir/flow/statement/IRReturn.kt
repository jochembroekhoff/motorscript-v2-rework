package nl.jochembroekhoff.motorscript.ir.flow.statement

class IRReturn(val type: Type) : IRStatementVertex() {
    enum class Type {
        VOID, EXPR
    }
}
