package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRPostfix(val op: Op) : IRExpressionVertex() {
    enum class Op(val repr: String) {
        DECR("--"),
        INCR("++")
    }
}
