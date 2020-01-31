package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRReturn
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRFlowVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class BlockVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) : MOSExtendedVisitor<Pair<IRFlowVertex, IRVertex>>(ectx, g) {
    override fun visitBlock(ctx: MOSParser.BlockContext): Pair<IRFlowVertex, IRFlowVertex> {
        // TODO: Implement
        return Pair(gMkV { IRReturn() }, gMkV { IRReturn() })
    }
}
