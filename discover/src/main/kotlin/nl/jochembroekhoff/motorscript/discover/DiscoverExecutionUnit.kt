package nl.jochembroekhoff.motorscript.discover

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.pack.PackIndex
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import java.util.concurrent.CompletableFuture

class DiscoverExecutionUnit : ExecutionUnit<PackIndex>() {

    companion object : KLogging()

    override fun executeInContext(ectx: ExecutionContext): Result<PackIndex, Unit> {
        val sourceIndex = ectx.executor.supply {
            logger.debug { "Indexing sources (treating source root in pack format)..." }
            return@supply PackIndex.loadFrom(ectx.execution.sourceRoot)
        }

        val dependencyResolvers = ectx.execution.buildSpec.dependencies.map { dependency ->
            ectx.executor.supply {
                logger.trace { "Resolving dependency ${dependency.format()}" }
                // TODO: Implement
            }
        }.toTypedArray()

        CompletableFuture.allOf(sourceIndex, *dependencyResolvers).get()

        return Ok(sourceIndex.get())
    }
}
