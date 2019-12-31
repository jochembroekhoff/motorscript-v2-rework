package nl.jochembroekhoff.motorscript.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import mu.KLogging
import nl.jochembroekhoff.motorscript.buildmgr.BuildManager
import nl.jochembroekhoff.motorscript.common.messages.Message
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors

object Main : KLogging() {
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("mosc")

        val optionBuildRoot by parser.option(
            ArgType.String,
            shortName = "r",
            description = "Path to build root",
            fullName = "buildroot"
        ).default(".")

        val optionJobs by parser.option(
            ArgType.Int,
            shortName = "j",
            description = "Amount of concurrent jobs, if less then 1, the number of available processors is used",
            fullName = "jobs"
        ).default(0)

        try {
            parser.parse(args)
        } catch (ex: IllegalStateException) {
            // Not using logger here
            println(ex.localizedMessage)
            return
        }

        val buildRoot = Paths.get(optionBuildRoot)

        if (!Files.exists(buildRoot) || !Files.isDirectory(buildRoot)) {
            logger.error { "The given build root does not exist or is not a directory: $optionBuildRoot" }
            return
        }

        val buildSpecFile = buildRoot.resolve("mosbuild.json")
        val sourceRoot = buildRoot.resolve("src")

        val messagePipe = object : MessagePipe {
            // TODO: Create MessagePipe that allows high parallel throughput, with buffer in ThreadLocal storage
            override fun dispatch(message: Message) {
                logger.trace { "Message: ${message.format()}" }
            }
        }

        when (val buildSpec = BuildManager.loadBuildSpec(buildSpecFile)) {
            is Error -> {
                logger.error { "Failed to load build spec: ${buildSpec.value}" }
                return
            }
            is Ok -> {
                when (val execution = BuildManager.createExecution(buildSpec.value, sourceRoot, messagePipe)) {
                    is Error -> {
                        logger.error { "Failed to create the execution: ${execution.value}" }
                    }
                    is Ok -> {
                        val actualNumJobs =
                            if (optionJobs <= 0) Runtime.getRuntime().availableProcessors()
                            else optionJobs

                        val executor = Executors.newFixedThreadPool(actualNumJobs, CustomThreadFactory())
                        logger.debug { "Created thread pool executor of $actualNumJobs thread(s)" }

                        BuildManager.runInExecutor(execution.value, executor)

                        executor.shutdown()
                    }
                }
            }
        }
    }
}
