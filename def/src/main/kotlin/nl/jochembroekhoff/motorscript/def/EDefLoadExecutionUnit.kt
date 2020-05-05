package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.extensions.tuples.mapSecond
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import java.nio.file.Files
import java.nio.file.Path

/**
 * Execution unit to load EDef files from dependencies. This ensures that all dependency metadata is loaded and valid to
 * be used. It will report failure when, for example, there are name clashes between dependencies.
 */
class EDefLoadExecutionUnit(private val dependencyLocations: Map<String, Path>) :
    ExecutionUnit<Map<String, EDefContainer>>() {

    companion object : KLogging()

    override fun execute(): Result<Map<String, EDefContainer>, Any> {
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

        val parsedDefs = gatherSafe(*futs)
            .mapOk { it.toMap() }
            .ifError { return it }
            .expect()

        val flatNames = computeFlatNameMap(parsedDefs)
            .withError {
                it.forEach { name, clashCausedBy ->
                    ectx.execution.messagePipe.dispatch(
                        Messages.nameClash.new(
                            "The name '${name.toInternalRepresentation()}' clashes between te following dependencies: ${clashCausedBy.joinToString(", ")}."
                        )
                    )
                }
                return Error(Unit)
            }
            .expect()

        return Ok(parsedDefs)
    }

    private fun computeFlatNameMap(defs: Map<String, EDefContainer>): Result<Map<NSID, String>, Map<NSID, Set<String>>> {
        // Keeps track of which names we've already seen + the name of the dependency that was responsible for that
        val seenNames: MutableMap<NSID, String> = HashMap()
        // Keeps track of which dependencies are involved in a name clash
        val clashes: MutableMap<NSID, MutableSet<String>> = HashMap()
        defs.forEach { dependencyName, dependencyContent ->
            dependencyContent.functions.map { it.first }.forEach { name ->
                val seenFor = seenNames[name]
                if (seenFor != null) {
                    clashes.computeIfAbsent(name) { HashSet() }.also {
                        if (logger.isDebugEnabled && seenFor !in it) {
                            logger.debug { "$name involved in clash was first seen for dependency $seenFor" }
                        }
                        it.add(seenFor)
                        it.add(dependencyName)
                    }
                } else {
                    seenNames[name] = dependencyName
                }
            }
        }

        return if (clashes.isNotEmpty()) {
            Error(clashes)
        } else {
            Ok(seenNames)
        }
    }
}
