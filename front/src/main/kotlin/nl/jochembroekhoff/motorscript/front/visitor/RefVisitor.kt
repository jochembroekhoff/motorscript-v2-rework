package nl.jochembroekhoff.motorscript.front.visitor

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.expression.IRRef
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class RefVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) : MOSExtendedVisitor<IRRef>(ectx, g) {
    companion object : KLogging()
    override fun visitRef(ctx: MOSParser.RefContext): IRRef {
        logger.debug { ctx.text }
        // TODO: Parse actual path
        return gMkV { IRRef(ctx.text) }
    }
}
