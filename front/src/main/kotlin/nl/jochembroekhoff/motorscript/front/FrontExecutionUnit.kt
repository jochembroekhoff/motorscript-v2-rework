package nl.jochembroekhoff.motorscript.front

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.front.results.BaseResult
import nl.jochembroekhoff.motorscript.front.results.FunctionResult
import nl.jochembroekhoff.motorscript.front.visitor.FunctionVisitor
import nl.jochembroekhoff.motorscript.front.visitor.ModifierVisitor
import nl.jochembroekhoff.motorscript.front.visitor.VisitorContext
import nl.jochembroekhoff.motorscript.ir.debugexport.IRDebugExporter
import nl.jochembroekhoff.motorscript.ir.graph.IREdge
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.ir.refs.ReferenceContext
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import nl.jochembroekhoff.motorscript.lexparse.util.SourceReferenceAttachmentUtil
import org.jgrapht.graph.SimpleDirectedGraph
import java.io.File

class FrontExecutionUnit(private val entries: Map<PackEntry, MOSParser.ScriptContext>) : ExecutionUnit<Unit>() {

    companion object : KLogging()

    override fun execute(): Result<Unit, Unit> {
        logger.debug { "Executing :front" }

        val debugFolder =
            File(ectx.execution.properties.getOrDefault("debug.export_location", "/tmp/mosc_debug_export"))
        debugFolder.mkdirs()

        val futs = entries.map { (k, v) ->
            ectx.executor.supply {
                return@supply processTopLevelItems(debugFolder, k, v)
            }
        }.toTypedArray()

        return gatherSafe(*futs)
            .mapOk { Unit }
            .mapError { errors -> errors.forEach { it.dispatchTo(ectx.execution.messagePipe) } }
    }

    private fun processTopLevelItems(
        debugFolder: File,
        packEntry: PackEntry,
        scriptContext: MOSParser.ScriptContext
    ): List<BaseResult> {
        val source = packEntry.file(ectx.execution.sourceRoot)

        return scriptContext.topLevelItem().map { tli ->
            tli.functionDeclaration()?.also { funcDecl ->
                val funcName = funcDecl.identifier().text

                val funcGraph = SimpleDirectedGraph<IRVertex, IREdge>(IREdge::class.java)
                // TODO: given ReferenceContext should be populated with imports
                val vctx = VisitorContext(ectx, funcGraph, ReferenceContext(packEntry.base))

                val modifiers = ModifierVisitor(vctx).visitFunctionModifiers(funcDecl.functionModifiers())
                // TODO: Check that k.name == funcName (only then something is allowed to be exported with a default modifier)
                val publishedName =
                    if (modifiers.contains(Modifier.DEFAULT))
                        packEntry.base / packEntry.name
                    else
                        packEntry.base / packEntry.name / funcName
                logger.debug { "Processing func '$funcName' (published as ${publishedName.toDebugString()})" }

                val funcVisitor = FunctionVisitor(vctx)
                val entryPoint = funcVisitor.visitFunctionBody(funcDecl.functionBody())

                // TMP
                IRDebugExporter.createDebugExporter().exportGraph(funcGraph, File(debugFolder, "$funcName.dot"))

                return@map FunctionResult()
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

            throw InternalAssertionExecutionException("Unreachable.")
        }
    }
}
