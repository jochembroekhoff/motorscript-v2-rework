package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.common.messages.Attachable
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.edge.IREdgeType
import nl.jochembroekhoff.motorscript.ir.graph.IRFlowVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.ir.graph.edge.IRBranchEdge
import nl.jochembroekhoff.motorscript.ir.graph.edge.IRDependencyEdge
import nl.jochembroekhoff.motorscript.ir.graph.edge.IRFlowEdge
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.BranchMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.DependencyMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.FlowMeta
import nl.jochembroekhoff.motorscript.lexparse.MOSBaseVisitor

abstract class MOSExtendedVisitor<T>(val vctx: VisitorContext) : MOSBaseVisitor<T>() {
    protected fun IRVertex.gDependOn(v: IRExpressionVertex, meta: DependencyMeta = DependencyMeta()): IREdge {
        val e = IRDependencyEdge(meta)
        vctx.g.addEdge(v, this, e)
        return e
    }

    protected fun IRFlowVertex.gFollowedBy(v: IRFlowVertex, meta: FlowMeta = FlowMeta()): IREdge {
        val e = IRFlowEdge(meta)
        vctx.g.addEdge(this, v, e)
        return e
    }

    protected fun IRFlowVertex.gBranchTo(v: IRFlowVertex, meta: BranchMeta = BranchMeta()): IREdge {
        val e = IRBranchEdge(meta)
        vctx.g.addEdge(this, v, e)
        return e
    }

    protected inline fun <T : IRVertex> gMkV(synthetic: Boolean = false, creator: () -> T): T {
        return creator().also {
            vctx.g.addVertex(it)
            if (synthetic) vctx.g.markSynthetic(it)
        }
    }

    /**
     * Get a [VisitorContext] for the next visitor instances, for after assignment.
     */
    protected fun vctxNext(): VisitorContext {
        return vctx.copy(rctx = vctx.rctx.next())
    }

    /**
     * Get a [VisitorContext] for a visitor that will visit a sub scope.
     */
    protected fun vctxNested(): VisitorContext {
        return vctx.copy(rctx = vctx.rctx.nested())
    }
}
