package nl.jochembroekhoff.motorscript.buildmgr

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import mu.KLogging
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

    fun runInExecutor(execution: Execution, executor: ExecutorService) {
        logger.trace { "Starting execution in executor" }

        val executionContext = ExecutionContext(execution, executor)

        val finalRes = DiscoverExecutionUnit().executeInContext(executionContext).then { discoverResult ->
            LexParseExecutionUnit(discoverResult).executeInContext(executionContext)
        }.then { lexParseRes ->
            FrontExecutionUnit(lexParseRes).executeInContext(executionContext)
        }
    }
}
