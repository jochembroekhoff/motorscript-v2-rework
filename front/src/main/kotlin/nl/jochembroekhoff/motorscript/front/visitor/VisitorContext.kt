package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import org.jgrapht.Graph

data class VisitorContext(val ectx: ExecutionContext, val g: Graph<IRVertex, IREdge>)
