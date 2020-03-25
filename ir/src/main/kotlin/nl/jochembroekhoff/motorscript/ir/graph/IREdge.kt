package nl.jochembroekhoff.motorscript.ir.graph

import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.EdgeMeta
import org.jgrapht.graph.DefaultEdge

class IREdge(val type: IREdgeType = IREdgeType.UNKNOWN, val meta: EdgeMeta) : DefaultEdge()
