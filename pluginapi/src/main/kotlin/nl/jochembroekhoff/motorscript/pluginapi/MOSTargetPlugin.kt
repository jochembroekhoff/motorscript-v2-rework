package nl.jochembroekhoff.motorscript.pluginapi

import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import java.util.*
import kotlin.streams.asSequence

abstract class MOSTargetPlugin {

    companion object {
        fun construct(targetPlatform: String, targetVersion: String): Sequence<Result<MOSTargetPlugin, Exception>> {
            return ServiceLoader.load(MOSTargetPlugin::class.java)
                .stream()
                .asSequence()
                .filter {
                    val targetFilters = it.type().kotlin.annotations.filterIsInstance<TargetFilter>()
                    targetFilters.isEmpty() || targetFilters.any { targetFilter ->
                        targetFilter.platform == targetPlatform && targetFilter.version == targetVersion
                    }
                }
                .map {
                    try {
                        Ok<MOSTargetPlugin, Exception>(it.get())
                    } catch (e: Exception) {
                        Error<MOSTargetPlugin, Exception>(e)
                    }
                }
        }
    }

    open fun init(targetPlatform: String, targetVersion: String) {}

    /**
     * Register tags into the given [tagRegistry]. The reference to this [tagRegistry] must not be kept.
     * The implementation may throw an exception when trying to register a tag after this method has returned.
     */
    open fun registerTags(tagRegistry: TagRegistry) {}

    /*
     * TODO:
     *  - Type registration. Function is a specialized type variant
     *  - Resource registration, like blocks and entity types
     */
}
