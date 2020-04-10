package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.common.messages.Attachable
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IREdgeType
import nl.jochembroekhoff.motorscript.ir.graph.IRFlowVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.BranchMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.DependencyMeta
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.FlowMeta
import nl.jochembroekhoff.motorscript.lexparse.MOSBaseVisitor

abstract class MOSExtendedVisitor<T>(val vctx: VisitorContext) : MOSBaseVisitor<T>() {
    protected fun IRVertex.gDependOn(v: IRVertex, meta: DependencyMeta = DependencyMeta()): IREdge {
        val e = IREdge(IREdgeType.DEPENDENCY, meta)
        vctx.g.addEdge(v, this, e)
        return e
    }

    protected fun IRFlowVertex.gFollowedBy(v: IRFlowVertex, meta: FlowMeta = FlowMeta()): IREdge {
        val e = IREdge(IREdgeType.FLOW, meta)
        vctx.g.addEdge(this, v, e)
        return e
    }

    protected fun IRFlowVertex.gBranchTo(v: IRFlowVertex, meta: BranchMeta = BranchMeta()): IREdge {
        val e = IREdge(IREdgeType.BRANCH, meta)
        vctx.g.addEdge(this, v, e)
        return e
    }

    protected inline fun <T : IRVertex> gMkV(synthetic: Boolean = false, creator: () -> T): T {
        return creator().also {
            vctx.g.addVertex(it)
            if (synthetic) vctx.g.markSynthetic(it)
        }
    }

    protected inline fun internalAssert(
        condition: Boolean,
        description: String,
        attachmentsProvider: () -> List<Attachable>
    ) {
        if (!condition) {
            throw InternalAssertionExecutionException(description, attachmentsProvider())
        }
    }

    protected fun internalAssert(condition: Boolean, description: String? = null) {
        if (!condition) {
            if (description == null) {
                throw InternalAssertionExecutionException()
            } else {
                throw InternalAssertionExecutionException(description)
            }
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
