package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRInfix(val op: Op) : IRExpressionVertex() {
    enum class Op(val repr: String) {
        MUL("*"),
        DIV("/"),
        MOD("%"),
        PLUS("+"),
        MINUS("-"),
    }
}
