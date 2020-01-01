package nl.jochembroekhoff.motorscript.lexparse

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.supply
import nl.jochembroekhoff.motorscript.common.pack.PackIndex
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import java.util.concurrent.CompletableFuture
import kotlin.streams.toList

class LexParseExecutionUnit(private val sourceIndex: PackIndex) : ExecutionUnit<Unit> {

    companion object : KLogging()

    override fun executeInContext(context: ExecutionContext): Result<Unit, Unit> {
        val futs = sourceIndex.streamType("mos").map { entry ->
            context.executor.supply {
                logger.debug { "LexParse for entry $entry" }
            }
        }.toList().toTypedArray()

        CompletableFuture.allOf(*futs).get()

        return Ok(Unit)
    }

}
