package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.lexparse.MOSBaseVisitor
import org.jgrapht.Graph

abstract class MOSExtendedVisitor<T : IRVertex>(val ectx: ExecutionContext, val g: Graph<IRVertex, IREdge>) :
    MOSBaseVisitor<T>()
