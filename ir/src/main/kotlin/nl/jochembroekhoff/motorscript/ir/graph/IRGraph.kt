package nl.jochembroekhoff.motorscript.ir.graph

import org.jgrapht.graph.SimpleDirectedGraph

class IRGraph : SimpleDirectedGraph<IRVertex, IREdge>(IREdge::class.java) {
    private val syntheticVertices: MutableSet<IRVertex> = HashSet()

    fun markSynthetic(v: IRVertex) {
        syntheticVertices.add(v)
    }

    fun isSynthetic(v: IRVertex): Boolean {
        return syntheticVertices.contains(v)
    }
}
