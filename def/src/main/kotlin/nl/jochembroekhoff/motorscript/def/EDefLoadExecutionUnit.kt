package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionException
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.extensions.tuples.mapSecond
import nl.jochembroekhoff.motorscript.common.result.Result
import java.nio.file.Files
import java.nio.file.Path

class EDefLoadExecutionUnit(private val dependencyLocations: Map<String, Path>) :
    ExecutionUnit<Map<String, EDefContainer>>() {

    companion object : KLogging()

    override fun execute(): Result<Map<String, EDefContainer>, List<ExecutionException>> {
        logger.debug { "Executing :def:eDefLoad" }

        val futs = dependencyLocations.asSequence()
            .map { it.toPair() }
            .map { dependency ->
                val json = Json(JsonConfiguration.Default)
                ectx.executor.supply {
                    dependency.mapSecond { dependencyLocation ->
                        json.parse(
                            DefContainer.serializer(
                                EDefContainerMeta.serializer(),
                                EDefFunctionMeta.serializer()
                            ),
                            Files.readString(dependencyLocation)
                        )
                    }
                }
            }
            .toList()
            .toTypedArray()

        return gatherSafe(*futs).mapOk { it.toMap() }
    }
}
