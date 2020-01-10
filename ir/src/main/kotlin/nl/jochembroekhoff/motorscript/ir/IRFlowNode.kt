package nl.jochembroekhoff.motorscript.ir

abstract class IRFlowNode : IRNode() {
    var parent: IRFlowNode? = null
}
