package nl.jochembroekhoff.motorscript.lexparse

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.pack.PackIndex
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import org.antlr.v4.runtime.*
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.streams.asSequence

class LexParseExecutionUnit(private val sourceIndex: PackIndex) : ExecutionUnit<Map<PackEntry, MOSParser.ScriptContext>> {

    companion object : KLogging()

    override fun executeInContext(context: ExecutionContext): Result<Map<PackEntry, MOSParser.ScriptContext>, Unit> {
        val anyFailed = AtomicBoolean(false)

        val futs = sourceIndex.streamType("mos").asSequence().associate { entry ->
            Pair(entry, context.executor.supply {
                val file = entry.file(context.execution.sourceRoot)

                logger.trace { "Processing $file" }

                val source: CharStream
                try {
                    source = CharStreams.fromPath(file)
                } catch (e: IOException) {
                    logger.error { "Failed to load $file" }
                    anyFailed.set(true)
                    return@supply null
                }
                val lexer = MOSLexer(source)
                val tokenStream = CommonTokenStream(lexer)
                val parser = MOSParser(tokenStream)

                // Prevent syntax errors from being logged to STDERR
                parser.removeErrorListener(ConsoleErrorListener.INSTANCE)

                val errorListener = MessagePipeErrorListener(file, context.execution.messagePipe)
                parser.addErrorListener(errorListener)

                try {
                    val parseRes = parser.script()
                    if (errorListener.errorCount > 0) {
                        anyFailed.set(true)
                    }
                    return@supply parseRes
                } catch (ignored: RecognitionException) {
                    anyFailed.set(true)
                    return@supply null
                }
            })
        }

        // Not expected to be able to throw anything
        CompletableFuture.allOf(*futs.values.toTypedArray()).get()

        return if (anyFailed.get()) {
            Error(Unit)
        } else {
            // Results of the futures are known to be non-null, otherwise anyFailed would have been true
            Ok(futs.mapValues { it.value.get()!! })
        }
    }
}
