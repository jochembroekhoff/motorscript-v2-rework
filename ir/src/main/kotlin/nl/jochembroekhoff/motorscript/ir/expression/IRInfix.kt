package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRInfix : IRExpressionVertex() {
    enum class Op(val repr: String) {
        MUL("*"),
        DIV("/"),
        MOD("%"),
        PLUS("+"),
        MINUS("-"),
    }
}
