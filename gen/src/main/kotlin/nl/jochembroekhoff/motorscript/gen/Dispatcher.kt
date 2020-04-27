package nl.jochembroekhoff.motorscript.gen

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.common.extensions.require
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.ir.flow.misc.IREntry
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRFlowVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRGraph
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.ir.graph.edge.IRFlowEdge
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Guard
import org.jgrapht.Graphs

class Dispatcher(private val id: NSID, private val g: IRGraph, private val publisher: Publisher) {

    val elementCounter = ElementCounter()
    private val knownElements: MutableMap<GenElement, GenContext> = HashMap()

    /* Utilities */

    private fun nextStatement(currentV: IRFlowVertex): IRStatementVertex? {
        val flowE = g.outgoingEdgesOf(currentV).asSequence().filterIsInstance<IRFlowEdge>().firstOrNull() ?: return null
        val nextV = Graphs.getOppositeVertex(g, flowE, currentV)
        if (nextV == null || nextV !is IRStatementVertex) {
            throw InternalAssertionExecutionException("Opposite vertex on flow edge is not a statement")
        }
        return nextV
    }

    private fun computeGuards(v: IRVertex): Set<Guard> {
        val res: MutableSet<Guard> = HashSet()
        g.incomingEdgesOf(v).asSequence()
            .filterIsInstance<IRFlowEdge>()
            .flatMap { it.meta.guard.asSequence() }
            .forEach { res.add(it) }
        return res
    }

    private fun registerContext(gctx: GenContext) {
        knownElements[gctx.currentOutput.element] = gctx
    }

    private fun nest(gctx: GenContext): GenContext {
        return gctx.copy(currentOutput = gctx.currentOutput.nest(this)).also { registerContext(it) }
    }

    private fun published(element: GenElement, baseId: NSID = id): NSID {
        return publisher.published(baseId, element)
    }

    /* Global generator logic */

    private fun generateStatements(baseGctx: GenContext, stmt: IRStatementVertex) {
        // TODO: Figure out which kind of statement this is
        // TODO: Process possible guards, if they are present, need to generate the guard AND make sure the gctx is
        //       updated with a possible new nested scope
        // TODO: Branching?

        var gctx = baseGctx
        var current: IRStatementVertex? = stmt
        while (current != null) {
            val guards = computeGuards(current)
            if (Guard.RETURN in guards) {
                // TODO: Generate proper guard to output
                val old = gctx
                gctx = nest(gctx)
                old.currentOutput.content.add("execute if score __did_ret MOS = 0 run function ${published(gctx.currentOutput.element).toGameRepresentation()}")
            }

            gctx.currentOutput.content.add("say hello")

            current = nextStatement(current)
        }
    }

    /* Public interface */

    /**
     * Start generating starting from the given [entryPoint].
     */
    fun generateStatementsStartingFrom(gctx: GenContext, entryPoint: IREntry) {
        registerContext(gctx)
        val nextV = nextStatement(entryPoint).require { "Entry point has no next flow vertex" }
        generateStatements(gctx, nextV)
    }

    fun generateExpression(gctx: GenContext, expr: IRExpressionVertex) {
        registerContext(gctx)
    }

    /**
     * Get a [Sequence] of all outputs of this [Dispatcher]. The first element of every [Pair] is the published ID, the
     * second element is a list of game commands.
     */
    fun collectOutput(): Sequence<Pair<NSID, List<String>>> {
        return knownElements.asSequence()
            .map { (element, gctx) -> Pair(published(element), gctx.currentOutput.content) }
    }
}
