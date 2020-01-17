package nl.jochembroekhoff.motorscript.front

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.lexparse.MOSParser
import nl.jochembroekhoff.motorscript.lexparse.SourceReferenceAttachmentTool
import java.util.concurrent.CompletableFuture

class FrontExecutionUnit(private val entries: Map<PackEntry, MOSParser.ScriptContext>) : ExecutionUnit<Unit> {

    companion object : KLogging()

    override fun executeInContext(context: ExecutionContext): Result<Unit, Unit> {
        logger.debug { "Executing :front" }

        val futs = entries.map { (k, v) ->
            context.executor.supply {
                val source = k.file(context.execution.sourceRoot)
                v.topLevelItem().forEach { tli ->
                    tli.functionDeclaration()?.also { funcDecl ->
                        val funcName = funcDecl.identifier().text
                        logger.info { "Processing func '$funcName'" }
                    }

                    tli.aliasDeclaration()?.also { aliasDecl ->
                        context.execution.messagePipe.dispatch(
                            Messages.notImplemented.new(
                                "Alias declarations are not implemented yet. The alias declaration is skipped.",
                                listOf(SourceReferenceAttachmentTool.fromTokenInFile(source, aliasDecl.start))
                            )
                        )
                    }

                    tli.typeSpecification()?.also { typeSpec ->
                        context.execution.messagePipe.dispatch(
                            Messages.notImplemented.new(
                                "Type specifications are not implemented yet. The type specification is skipped.",
                                listOf(SourceReferenceAttachmentTool.fromTokenInFile(source, typeSpec.start))
                            )
                        )
                    }

                    tli.declarationStatement()?.also { contDecl ->
                        context.execution.messagePipe.dispatch(
                            Messages.notImplemented.new(
                                "Top-level container declarations are not implemented yet. The container declaration is skipped.",
                                listOf(SourceReferenceAttachmentTool.fromTokenInFile(source, contDecl.start))
                            )
                        )
                    }
                }
            }
        }.toTypedArray()

        CompletableFuture.allOf(*futs).get()

        return Ok(Unit)
    }
}
