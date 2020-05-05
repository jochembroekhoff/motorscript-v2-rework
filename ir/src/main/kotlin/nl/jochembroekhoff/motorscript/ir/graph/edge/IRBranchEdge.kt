package nl.jochembroekhoff.motorscript.ir.graph.edge

import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.BranchMeta

class IRBranchEdge(override val meta: BranchMeta) : IREdge(IREdgeType.BRANCH, meta)
