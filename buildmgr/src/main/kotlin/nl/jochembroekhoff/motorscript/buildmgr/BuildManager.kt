package nl.jochembroekhoff.motorscript.buildmgr

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KLogging
import nl.jochembroekhoff.motorscript.buildmgr.apiimpl.AggregateTagRegistrar
import nl.jochembroekhoff.motorscript.buildmgr.apiimpl.AggregateTypeRegistrar
import nl.jochembroekhoff.motorscript.buildmgr.apiimpl.RegistryImpl
import nl.jochembroekhoff.motorscript.buildmgr.externalimpl.ExternalFunction
import nl.jochembroekhoff.motorscript.buildmgr.externalimpl.ExternalPlugin
import nl.jochembroekhoff.motorscript.check.CheckExecutionUnit
import nl.jochembroekhoff.motorscript.common.buildspec.BuildSpec
import nl.jochembroekhoff.motorscript.common.execution.Execution
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionException
import nl.jochembroekhoff.motorscript.common.extensions.path.div
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.common.result.then
import nl.jochembroekhoff.motorscript.def.EDefBundle
import nl.jochembroekhoff.motorscript.def.EDefLoadExecutionUnit
import nl.jochembroekhoff.motorscript.discover.DiscoverExecutionUnit
import nl.jochembroekhoff.motorscript.front.FrontExecutionUnit
import nl.jochembroekhoff.motorscript.gen.GenExecutionUnit
import nl.jochembroekhoff.motorscript.lexparse.LexParseExecutionUnit
import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
import nl.jochembroekhoff.motorscript.pluginapi.registration.TypeRegistrar
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ExecutorService

object BuildManager : KLogging() {
    fun loadBuildSpec(source: Path): Result<BuildSpec, String> {
        if (!Files.exists(source)) {
            return Error("Build spec at $source does not exist.")
        }

        val input = Files.readString(source)
        val json = Json(JsonConfiguration.Default)

        return try {
            Ok(json.parse(BuildSpec.serializer(), input))
        } catch (ex: SerializationException) {
            Error(ex.message ?: "")
        }
    }

    fun createExecution(
        buildSpec: BuildSpec,
        sourceRoot: Path,
        outputRoot: Path,
        messagePipe: MessagePipe,
        properties: Map<String, String>
    ): Result<Execution, String> {
        logger.debug { "Creating execution from source root $sourceRoot" }

        if (!Files.exists(sourceRoot) || !Files.isDirectory(sourceRoot)) {
            return Error("The source root located at $sourceRoot does not exist or is not a directory.")
        }

        if (Files.exists(outputRoot) && !Files.isDirectory(outputRoot)) {
            return Error("The output root located at $outputRoot exists, but it not a directory.")
        } else {
            try {
                Files.createDirectories(outputRoot)
            } catch (e: IOException) {
                return Error("Failed to create the output root: ${e.message}.")
            }
        }

        return Ok(
            Execution(
                buildSpec,
                sourceRoot,
                outputRoot,
                messagePipe,
                properties
            )
        )
    }

    /**
     * Run an [execution] in an [executor].
     * @return `true` if the entire operation was successful, `false` otherwise. Errors will be sent to the [MessagePipe] that is attached to the [execution].
     */
    fun runInExecutor(
        execution: Execution,
        executor: ExecutorService,
        stageChanged: ((buildStage: BuildStage) -> Unit)?
    ): Boolean {
        logger.trace { "Starting execution in executor" }

        val executionContext = ExecutionContext(execution, executor)

        stageChanged?.invoke(BuildStage.LOAD)

        val frontRes = DiscoverExecutionUnit().executeInContext(executionContext).withError { err ->
            logger.trace { "Discover failed with $err" }
        }.then { (sourceIndex, dependencyLocations) ->
            EDefLoadExecutionUnit(dependencyLocations).executeInContext(executionContext)
                .mapOk { Pair(sourceIndex, it) }
        }.then { (sourceIndex, eDefBundle) ->
            LexParseExecutionUnit(sourceIndex).executeInContext(executionContext)
                .mapOk { Pair(it, eDefBundle) }
        }.then { (lexParseRes, eDefBundle) ->
            FrontExecutionUnit(lexParseRes).executeInContext(executionContext)
                .mapOk { Pair(it, eDefBundle) }
        }

        if (frontRes !is Ok) {
            logger.info { "Generic build execution part failed, see message pipe. Result: $frontRes" }
            frontRes.withError { tryDispatchErrorsToMessagePipe(it, executionContext.execution.messagePipe) }
            return false
        }

        logger.trace { "Generic build execution part completed successfully. Heading over to target-specific parts" }

        val (irContainers, eDefBundle) = frontRes.value

        execution.buildSpec.targets.forEach { target ->
            logger.info { "Processing target $target" }

            stageChanged?.invoke(BuildStage.TARGET_COMPILE)

            val targetOutputDirectory = executionContext.execution.outputRoot / target.platform / target.version
            try {
                Files.createDirectories(targetOutputDirectory)
            } catch (e: IOException) {
                logger.error(e) { "Failed to create the output directory for $target" }
                return false
            }

            // TODO: Split these couple of things (initializing plug-ins, populating registries, etc.) off into a
            //       dedicated execution unit

            logger.debug { "Constructing and initializing plug-ins" }

            val pluginInstances = MOSTargetPlugin.construct(target.platform, target.version)
                .onEach {
                    it.withError { err ->
                        // TODO: Dispatch error message to message pipe that a plugin failed to be instantiated,
                        //       instead of only logging it
                        logger.error(err) { "Plugin failed to be instantiated" }
                    }
                }
                .filterIsInstance<Ok<MOSTargetPlugin, *>>()
                .map { it.value }
                .onEach { plugin ->
                    plugin.init(target.platform, target.version)
                }
                .toSet()

            val mutableRegistry = RegistryImpl()

            logger.debug { "Populating registry with plugins" }

            val tagRegistrar = AggregateTagRegistrar(mutableRegistry)
            val typeRegistrar = AggregateTypeRegistrar(mutableRegistry)

            pluginInstances.forEach { plugin ->
                logger.trace { "Allowing plugin ${plugin.name} to register" }
                tagRegistrar.createInstanceFor(plugin).also {
                    plugin.registerTags(it)
                }
                typeRegistrar.createInstanceFor(plugin).also {
                    plugin.registerTypes(it)
                }
            }

            logger.debug { "Populating registry with EDef data" }

            val externalPlugin = ExternalPlugin()
            val externalTypeRegistry = typeRegistrar.createInstanceFor(externalPlugin)
            populateFromEDefBundle(eDefBundle, externalTypeRegistry)

            logger.debug { "Populating registry with IR data" }

            // TODO: Populate registry with IR data

            logger.debug { "Starting to execute target-specific execution units" }

            val result =
                CheckExecutionUnit(irContainers, mutableRegistry).executeInContext(executionContext).then { checkRes ->
                    stageChanged?.invoke(BuildStage.TARGET_WRITE)
                    GenExecutionUnit(targetOutputDirectory, checkRes, mutableRegistry).executeInContext(executionContext)
                }

            stageChanged?.invoke(BuildStage.TARGET_DONE)

            result.withError { err ->
                logger.info { "Target $target failed, see message pipe. Result: $result" }
                tryDispatchErrorsToMessagePipe(err, executionContext.execution.messagePipe)
            }
        }

        stageChanged?.invoke(BuildStage.DONE)

        return true
    }

    private fun populateFromEDefBundle(eDefBundle: EDefBundle, typeRegistry: TypeRegistrar) {
        // TODO: Load tags from the EDefBundle (once they are supported in EDefs)
        eDefBundle.containers.values.asSequence()
            .flatMap { it.functions }
            .forEach { (name, functionMeta) ->
                // TODO: Might want to propagate the name of the dependency to the instance of ExternalFunction,
                //       for later reference
                typeRegistry.registerType(name, ExternalFunction(functionMeta.signature))
            }
    }

    /**
     * Try to extract [ExecutionException]s from an [error] value (technically other values work as well), and if they are
     * found, dispatch them to the given [messagePipe].
     */
    private fun tryDispatchErrorsToMessagePipe(error: Any?, messagePipe: MessagePipe) {
        if (error is Iterable<*>) {
            error.filterIsInstance<ExecutionException>()
                .forEach { it.dispatchTo(messagePipe) }
        }
    }
}
