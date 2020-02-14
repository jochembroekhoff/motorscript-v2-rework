package nl.jochembroekhoff.motorscript.ir.graph

abstract class IRExpressionVertex : IRVertex() {
    override fun contentClass(): String {
        return "EXPR"
    }
}
