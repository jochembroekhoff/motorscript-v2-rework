package nl.jochembroekhoff.motorscript.ir.graph.edge

import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.DependencyMeta

class IRDependencyEdge(override val meta: DependencyMeta) : IREdge(IREdgeType.DEPENDENCY, meta)
