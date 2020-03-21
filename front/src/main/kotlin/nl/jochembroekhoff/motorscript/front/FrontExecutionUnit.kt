package nl.jochembroekhoff.motorscript.front

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.front.visitor.FunctionVisitor
import nl.jochembroekhoff.motorscript.front.visitor.VisitorContext
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.ir.refs.ReferenceContext
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import nl.jochembroekhoff.motorscript.lexparse.util.SourceReferenceAttachmentUtil
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.nio.AttributeType
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
import java.io.File

class FrontExecutionUnit(private val entries: Map<PackEntry, MOSParser.ScriptContext>) : ExecutionUnit<Unit>() {

    companion object : KLogging()

    override fun executeInContext(ectx: ExecutionContext): Result<Unit, Unit> {
        logger.debug { "Executing :front" }

        val tmpFolder = File("/tmp/mosg")
        tmpFolder.mkdirs()

        val futs = entries.map { (k, v) ->
            ectx.executor.supply {
                val source = k.file(ectx.execution.sourceRoot)

                v.topLevelItem().forEach { tli ->
                    tli.functionDeclaration()?.also { funcDecl ->
                        val funcName = funcDecl.identifier().text
                        logger.debug { "Processing func '$funcName'" }
                        val funcGraph = SimpleDirectedGraph<IRVertex, IREdge>(IREdge::class.java)
                        // TODO: given ReferenceContext should be populated with imports
                        val vctx = VisitorContext(ectx, funcGraph, ReferenceContext(k.base))
                        val funcVisitor = FunctionVisitor(vctx)
                        val entryPoint = funcVisitor.visitFunctionBody(funcDecl.functionBody())

                        // TMP
                        val exporter = DOTExporter<IRVertex, IREdge>().apply {
                            setVertexAttributeProvider { vertex ->
                                val base = "${vertex.contentClass()} : ${vertex::class.java.simpleName}"
                                val description = vertex.contentDescription()
                                val label = if (description.isNotBlank()) {
                                    "$base\n$description"
                                } else {
                                    base
                                }
                                mapOf("label" to DefaultAttribute(label, AttributeType.STRING))
                            }
                            setEdgeAttributeProvider { edge ->
                                val label = edge.type.toString()
                                mapOf("label" to DefaultAttribute(label, AttributeType.STRING))
                            }
                        }

                        exporter.exportGraph(funcGraph, File(tmpFolder, "$funcName.dot"))
                    }

                    tli.aliasDeclaration()?.also { aliasDecl ->
                        throw FeatureUnimplementedExecutionException(
                            "Alias declarations are not implemented yet.",
                            listOf(SourceReferenceAttachmentUtil.fromTokenInFile(source, aliasDecl.start))
                        )
                    }

                    tli.typeSpecification()?.also { typeSpec ->
                        throw FeatureUnimplementedExecutionException(
                            "Type specifications are not implemented yet.",
                            listOf(SourceReferenceAttachmentUtil.fromTokenInFile(source, typeSpec.start))
                        )
                    }

                    tli.declarationStatement()?.also { contDecl ->
                        throw FeatureUnimplementedExecutionException(
                            "Top-level container declarations are not implemented yet.",
                            listOf(SourceReferenceAttachmentUtil.fromTokenInFile(source, contDecl.start))
                        )
                    }
                }
            }
        }.toTypedArray()

        return gatherSafe(*futs)
            .mapOk { Unit }
            .mapError { errors -> errors.forEach { it.dispatchTo(ectx.execution.messagePipe) } }
    }
}
