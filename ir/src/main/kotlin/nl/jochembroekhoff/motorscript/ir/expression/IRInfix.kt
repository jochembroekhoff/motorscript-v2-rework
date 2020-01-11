package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.IRExpressionNode

class IRInfix : IRExpressionNode() {
    enum class Op(val repr: String) {
        MUL("*"),
        DIV("/"),
        MOD("%"),
        PLUS("+"),
        MINUS("-"),
    }
}
