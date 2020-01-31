package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

abstract class IRLiteral<T>(val stringValue: String) : IRExpressionVertex() {
    abstract val value: T
}
