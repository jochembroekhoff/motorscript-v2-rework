package nl.jochembroekhoff.motorscript.front.visitor

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.ir.graph.IRGraph
import nl.jochembroekhoff.motorscript.ir.refs.ReferenceContext

data class VisitorContext(val ectx: ExecutionContext, val g: IRGraph, val rctx: ReferenceContext)
