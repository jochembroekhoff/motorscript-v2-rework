package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRFind(val type: Type) : IRExpressionVertex() {
    enum class Type {
        INDEX,
        PATH
    }
}
