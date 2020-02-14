package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex

class IRRef(val text: String) : IRExpressionVertex() {
    override fun contentDescription(): String {
        return "Text: $text"
    }
}
