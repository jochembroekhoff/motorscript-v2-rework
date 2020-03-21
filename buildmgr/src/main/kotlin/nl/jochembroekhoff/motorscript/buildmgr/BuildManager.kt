package nl.jochembroekhoff.motorscript.buildmgr

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KLogging
import nl.jochembroekhoff.motorscript.check.CheckExecutionUnit
import nl.jochembroekhoff.motorscript.common.buildspec.BuildSpec
import nl.jochembroekhoff.motorscript.common.execution.Execution
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.discover.DiscoverExecutionUnit
import nl.jochembroekhoff.motorscript.front.FrontExecutionUnit
import nl.jochembroekhoff.motorscript.lexparse.LexParseExecutionUnit
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

    fun createExecution(buildSpec: BuildSpec, sourceRoot: Path, messagePipe: MessagePipe): Result<Execution, String> {
        logger.debug { "Creating execution from source root $sourceRoot" }

        if (!Files.exists(sourceRoot) || !Files.isDirectory(sourceRoot)) {
            return Error("The source root located at $sourceRoot does not exist or is not a directory.")
        }

        return Ok(
            Execution(
                buildSpec,
                sourceRoot,
                messagePipe
            )
        )
    }

    /**
     * Run an [execution] in an [executor].
     * @return `true` if the entire operation was successful, `false` otherwise. Errors will be sent to the [MessagePipe]. that is attached to the [execution].
     */
    fun runInExecutor(execution: Execution, executor: ExecutorService): Boolean {
        logger.trace { "Starting execution in executor" }

        val executionContext = ExecutionContext(execution, executor)

        val frontRes = DiscoverExecutionUnit().executeInContext(executionContext).withError { err ->
            logger.trace { "Discover failed with $err" }
        }.then { discoverResult ->
            LexParseExecutionUnit(discoverResult).executeInContext(executionContext)
        }.withError { err ->
            logger.trace { "LexParse failed with $err" }
        }.then { lexParseRes ->
            FrontExecutionUnit(lexParseRes).executeInContext(executionContext)
        }

        if (frontRes !is Ok) {
            logger.info { "Generic build execution part failed, see message pipe. Result: $frontRes" }
            return false
        }

        logger.trace { "Generic build execution part completed successfully. Heading over to target-specific parts" }

        execution.buildSpec.targets.forEach { target ->
            logger.info { "Processing target $target" }

            val result = CheckExecutionUnit().executeInContext(executionContext)
        }

        return true
    }
}
