package nl.jochembroekhoff.motorscript.discover

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.buildspec.Dependency
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.pack.PackIndex
import nl.jochembroekhoff.motorscript.common.result.Result
import java.nio.file.Path
import java.nio.file.Paths

class DiscoverExecutionUnit : ExecutionUnit<Pair<PackIndex, Map<String, Path>>>() {

    companion object : KLogging()

    override fun execute(): Result<Pair<PackIndex, Map<String, Path>>, Any?> {
        val sourceIndex = ectx.executor.supply {
            logger.debug { "Indexing sources (treating source root in pack format)..." }
            return@supply PackIndex.loadFrom(ectx.execution.sourceRoot)
        }

        val dependencies = ectx.execution.buildSpec.dependencies
        val dependencySequence =
            if (ectx.execution.buildSpec.name != "stdlib" && dependencies.none { it.name == "stdlib" }) {
                logger.debug { "Adding implicit stdlib dependency" }
                sequenceOf(Dependency("stdlib", "*"))
            } else {
                logger.debug { "stdlib dependency is already present, or project is stdlib itself" }
                emptySequence()
            } + dependencies.asSequence()

        val dependencyResolvers = dependencySequence
            .map { dependency ->
                ectx.executor.supply {
                    logger.trace { "Resolving dependency ${dependency.format()}" }
                    if (dependency.name != "stdlib") {
                        TODO("Other dependencies then stdlib")
                    }
                    Pair(dependency.name, Paths.get("examples/stdlib/edef.json"))
                }
            }
            .toList()
            .toTypedArray()

        val resolverResults = gatherSafe(*dependencyResolvers)
        return resolverResults.mapOk { Pair(sourceIndex.get(), it.toMap()) }
    }
}
