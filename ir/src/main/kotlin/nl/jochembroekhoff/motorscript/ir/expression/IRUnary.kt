package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRUnary(val op: Op) : IRExpressionVertex() {
    enum class Op(val repr: String) {
        NEGATE("!"),
        PRE_DECR("--"),
        PRE_INCR("++")
    }
}
