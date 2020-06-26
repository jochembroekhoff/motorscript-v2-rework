package nl.jochembroekhoff.motorscript.pluginapi

import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.pluginapi.registration.TagRegistrar
import nl.jochembroekhoff.motorscript.pluginapi.registration.TypeRegistrar
import java.util.*
import kotlin.streams.asSequence

abstract class MOSTargetPlugin {

    companion object {
        /**
         * Construct plugin instances for a combination of [targetPlatform] and [targetVersion].
         *
         * The plugins are discovered using Java SPI. Any valid service provider for the [MOSTargetPlugin] class will be
         * used.
         *
         * If the service implementation class contains at least one [TargetFilter] annotation, the configuration of
         * these annotations is respected.
         *
         * As this method returns a [Sequence], the service implementations are discovered and loaded lazily.
         */
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

    /**
     * Name of the plugin, for identification purposes.
     */
    abstract val name: String

    /**
     * Optional description of the plugin. To be showed to user.
     */
    open val description: String? = null

    /**
     * Initialize the plugin. The [targetPlatform] and [targetVersion] indicate for which platform (and which version
     * thereof) the plugin will be used. The plugin may behave differently based on the given parameter values.
     *
     * If any [TargetFilter] is present on the class, the [targetPlatform] and [targetVersion] are guaranteed to be one
     * of the allowed combinations.
     */
    open fun init(targetPlatform: String, targetVersion: String) {}

    /**
     * Register tags into the given [tagRegistry]. The reference to this [tagRegistry] must not be kept.
     * The implementation may throw an exception when trying to register a tag after this method has returned.
     */
    open fun registerTags(tagRegistry: TagRegistrar) {}

    /**
     * Register types into the given [typeRegistry]. The reference to this [typeRegistry] must not be kept.
     * The implementation may throw an exception when trying to register a type after this method has returned.
     */
    open fun registerTypes(typeRegistry: TypeRegistrar) {}

    /**
     * TODO: Implement
     */
    open fun registerResources() {}
}
