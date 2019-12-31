package nl.jochembroekhoff.motorscript.discover

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.supply
import nl.jochembroekhoff.motorscript.discover.pack.PackLoader
import java.util.concurrent.CompletableFuture

class DiscoverExecutionUnit : ExecutionUnit {

    companion object : KLogging()

    override fun executeInContext(context: ExecutionContext): Boolean {
        val sourceIndex = context.executor.supply {
            logger.debug { "Indexing sources (treating source root in pack format)..." }
            return@supply PackLoader.loadFrom(context.execution.sourceRoot)
        }

        val dependencyResolvers = context.execution.buildSpec.dependencies.map { dependency ->
            context.executor.supply {
                logger.trace { "Resolving dependency ${dependency.format()}" }
                // TODO: Implement
            }
        }.toTypedArray()

        CompletableFuture.allOf(sourceIndex, *dependencyResolvers).get()

        return true
    }
}
