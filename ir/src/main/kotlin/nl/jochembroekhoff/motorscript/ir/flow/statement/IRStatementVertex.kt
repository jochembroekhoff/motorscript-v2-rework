package nl.jochembroekhoff.motorscript.ir.flow.statement

import nl.jochembroekhoff.motorscript.ir.graph.IRFlowVertex

abstract class IRStatementVertex : IRFlowVertex() {
    override fun contentClass(): String {
        return "STMT"
    }
}
