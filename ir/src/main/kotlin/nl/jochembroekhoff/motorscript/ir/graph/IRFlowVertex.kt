package nl.jochembroekhoff.motorscript.ir.graph

abstract class IRFlowVertex : IRVertex() {
    override fun contentClass(): String {
        return "FLOW"
    }
}
