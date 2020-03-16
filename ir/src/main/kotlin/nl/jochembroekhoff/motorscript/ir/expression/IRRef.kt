package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.refs.ReferenceContext

class IRRef(val text: String, val rctx: ReferenceContext) : IRExpressionVertex() {
    override fun contentDescription(): String {
        return "Text: $text"
    }
}
