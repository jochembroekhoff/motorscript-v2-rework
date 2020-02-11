package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRCompare(val type: Type) : IRExpressionVertex() {
    enum class Type {
        IS,
        MATCHES,
        LT,
        LTE,
        GT,
        GTE,
        EQ
    }
}
