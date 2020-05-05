package nl.jochembroekhoff.motorscript.ir.graph.edge

import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.FlowMeta

class IRFlowEdge(override val meta: FlowMeta) : IREdge(IREdgeType.FLOW, meta)
