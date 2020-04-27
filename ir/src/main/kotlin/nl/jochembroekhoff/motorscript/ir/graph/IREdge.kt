package nl.jochembroekhoff.motorscript.ir.graph

import nl.jochembroekhoff.motorscript.ir.graph.edge.IREdgeType
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.EdgeMeta
import org.jgrapht.graph.DefaultEdge

abstract class IREdge(val type: IREdgeType = IREdgeType.UNKNOWN, open val meta: EdgeMeta) : DefaultEdge()
