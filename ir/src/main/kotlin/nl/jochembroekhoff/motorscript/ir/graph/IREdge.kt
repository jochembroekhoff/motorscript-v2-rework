package nl.jochembroekhoff.motorscript.ir.graph

import org.jgrapht.graph.DefaultEdge

class IREdge(val type: IREdgeType = IREdgeType.UNKNOWN) : DefaultEdge()
