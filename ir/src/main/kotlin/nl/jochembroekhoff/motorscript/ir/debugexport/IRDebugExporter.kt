package nl.jochembroekhoff.motorscript.ir.debugexport

import nl.jochembroekhoff.motorscript.ir.flow.misc.IREntry
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IREdgeType
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.GraphExporter
import org.jgrapht.nio.dot.DOTExporter

object IRDebugExporter {
    fun createDebugExporter(): GraphExporter<IRVertex, IREdge> {
        return DOTExporter<IRVertex, IREdge>().apply {
            setVertexAttributeProvider { vertex ->
                val base = "${vertex.contentClass()} : ${vertex::class.java.simpleName}"
                val description = vertex.contentDescription()
                val label = if (description.isNotBlank()) {
                    "$base\n$description"
                } else {
                    base
                }
                val shape = when (vertex) {
                    is IREntry -> "hexagon"
                    is IRStatementVertex -> "box"
                    else -> "ellipse"
                }
                mapOf(
                    "label" to DefaultAttribute.createAttribute(label),
                    "shape" to DefaultAttribute.createAttribute(shape)
                )
            }
            setEdgeAttributeProvider { edge ->
                val label = edge.type.toString() + "\n" + edge.meta.contentDescription()
                val color = when (edge.type) {
                    IREdgeType.FLOW -> "green"
                    IREdgeType.BRANCH -> "aqua"
                    else -> "black"
                }
                val style = when (edge.type) {
                    IREdgeType.DEPENDENCY -> "dotted"
                    else -> ""
                }
                mapOf(
                    "label" to DefaultAttribute.createAttribute(label),
                    "color" to DefaultAttribute.createAttribute(color),
                    "style" to DefaultAttribute.createAttribute(style)
                )
            }
        }
    }
}
