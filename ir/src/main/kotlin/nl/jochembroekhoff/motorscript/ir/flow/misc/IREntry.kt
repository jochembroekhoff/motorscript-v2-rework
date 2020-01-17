package nl.jochembroekhoff.motorscript.ir.flow.misc

import nl.jochembroekhoff.motorscript.ir.graph.IRFlowVertex
import nl.jochembroekhoff.motorscript.ir.flow.IRSingleFlowVertex

class IREntry(previous: IRFlowVertex?, next: IRFlowVertex?) : IRSingleFlowVertex() {
    // should have only one next branch
    // should have reference to all return statements
}
