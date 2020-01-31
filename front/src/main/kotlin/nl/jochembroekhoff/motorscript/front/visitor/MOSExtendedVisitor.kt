package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IREdgeType
import nl.jochembroekhoff.motorscript.ir.graph.IRFlowVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSBaseVisitor
import org.jgrapht.Graph

abstract class MOSExtendedVisitor<T>(val ectx: ExecutionContext, val g: Graph<IRVertex, IREdge>) : MOSBaseVisitor<T>() {
    protected fun IRVertex.gDependOn(v: IRVertex): IREdge {
        val e = IREdge(IREdgeType.DEPENDENCY)
        g.addEdge(this, v, e)
        return e
    }

    protected fun IRFlowVertex.gFollowedBy(v: IRFlowVertex): IREdge {
        val e = IREdge(IREdgeType.FLOW)
        g.addEdge(this, v, e)
        return e
    }

    protected inline fun <T : IRVertex> gMkV(crossinline creator: () -> T): T {
        return creator().also { g.addVertex(it) }
    }
}
