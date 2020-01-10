package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.IRExpressionNode

class IRInfix : IRExpressionNode() {
    enum class Op(val repr: String, val commutative: Boolean) {
        MUL("*", true),
        DIV("/", false),
        MOD("%", false),
        PLUS("+", true),
        MINUS("-", false),
    }
}
