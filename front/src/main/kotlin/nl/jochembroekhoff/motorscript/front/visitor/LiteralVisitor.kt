package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteral
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import org.jgrapht.Graph

class LiteralVisitor(ectx: ExecutionContext, g: Graph<IRVertex, IREdge>) : MOSExtendedVisitor<IRLiteral<*>>(ectx, g) {
    override fun visitLiteralString(ctx: MOSParser.LiteralStringContext): IRLiteral<*> {
        TODO()
    }
}
