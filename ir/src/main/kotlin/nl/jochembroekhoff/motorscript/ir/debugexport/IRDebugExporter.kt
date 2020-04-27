package nl.jochembroekhoff.motorscript.ir.debugexport

import nl.jochembroekhoff.motorscript.ir.flow.misc.IREntry
import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.edge.IREdgeType
import nl.jochembroekhoff.motorscript.ir.graph.IRGraph
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import org.jgrapht.nio.Attribute
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
import java.io.File

object IRDebugExporter {
    fun exportGraph(g: IRGraph, dest: File) {
        DOTExporter<IRVertex, IREdge>().run {
            setVertexAttributeProvider { v ->
                val base = "${v.contentClass()} : ${v::class.java.simpleName}" +
                    if (g.isSynthetic(v)) " *" else ""
                val description = v.contentDescription()
                val label = if (description.isNotBlank()) {
                    "$base\n$description"
                } else {
                    base
                }
                val shape = when (v) {
                    is IREntry -> "hexagon"
                    is IRStatementVertex -> "box"
                    else -> "ellipse"
                }
                mapOf(
                    "label" to attr(label),
                    "shape" to attr(shape)
                )
            }
            setEdgeAttributeProvider { e ->
                val label = e.type.toString() + "\n" + e.meta.contentDescription()
                val color = when (e.type) {
                    IREdgeType.FLOW -> "green"
                    IREdgeType.BRANCH -> "aqua"
                    else -> "black"
                }
                val style = when (e.type) {
                    IREdgeType.DEPENDENCY -> "dotted"
                    else -> ""
                }
                mapOf(
                    "label" to attr(label),
                    "color" to attr(color),
                    "style" to attr(style)
                )
            }
            exportGraph(g, dest)
        }
    }

    private fun attr(value: String): Attribute {
        return DefaultAttribute.createAttribute(value.replace("\\", "\\\\"))
    }
}
