package nl.jochembroekhoff.motorscript.ir.graph

import nl.jochembroekhoff.motorscript.common.extensions.sequences.pairRight
import nl.jochembroekhoff.motorscript.ir.graph.edge.IRDependencyEdge
import org.jgrapht.Graphs
import org.jgrapht.graph.SimpleDirectedGraph

class IRGraph : SimpleDirectedGraph<IRVertex, IREdge>(IREdge::class.java) {
    private val syntheticVertices: MutableSet<IRVertex> = HashSet()

    fun markSynthetic(v: IRVertex) {
        syntheticVertices.add(v)
    }

    fun isSynthetic(v: IRVertex): Boolean {
        return syntheticVertices.contains(v)
    }

    fun dependenciesOf(v: IRVertex): Sequence<Pair<IRDependencyEdge, IRExpressionVertex>> {
        @Suppress("UNCHECKED_CAST")
        return incomingEdgesOf(v).asSequence()
            .filterIsInstance<IRDependencyEdge>()
            .pairRight { e -> Graphs.getOppositeVertex(this, e, v) }
            .filter { it.second is IRExpressionVertex } as Sequence<Pair<IRDependencyEdge, IRExpressionVertex>>
    }

    /**
     * Replace the vertex [search] with the vertex [replace]. Edges are kept intact.
     */
    fun replaceVertex(search: IRVertex, replace: IRVertex) {
        addVertex(replace)

        if (isSynthetic(search)) {
            syntheticVertices.remove(search)
            markSynthetic(replace)
        }

        outgoingEdgesOf(search).forEach { e ->
            removeEdge(e) // might not be necessary
            addEdge(replace, getEdgeTarget(e), e)
        }
        incomingEdgesOf(search).forEach { e ->
            removeEdge(e) // might not be necessary
            addEdge(replace, getEdgeSource(e), e)
        }

        removeVertex(search)
    }
}
