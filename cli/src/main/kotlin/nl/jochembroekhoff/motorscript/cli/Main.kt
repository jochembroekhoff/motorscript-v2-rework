package nl.jochembroekhoff.motorscript.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple
import mu.KLogging
import nl.jochembroekhoff.motorscript.buildmgr.BuildManager
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
            fullName = "buildroot",
            description = "Path to build root"
        ).default(".")

        val optionDebugFlags by parser.option(
            ArgType.Choice(listOf("export_graph_front")),
            shortName = "d",
            fullName = "debug_flag",
            description = "Debug flag to enable"
        ).multiple()

        val optionJobs by parser.option(
            ArgType.Int,
            shortName = "j",
            fullName = "jobs",
            description = "Amount of concurrent jobs, if less then 1, the number of available processors is used"
        ).default(0)

        val optionProperties by parser.option(
            KeyValueArgType(),
            shortName = "P",
            fullName = "property",
            description = "General-purpose property"
        ).multiple()

        try {
            parser.parse(args)
        } catch (ex: IllegalStateException) {
            // Not using logger here
            println(ex.localizedMessage)
            return
        }

        /*
         * Basic args processing
         */

        val propertyMap = optionProperties.toMap()
        val debugFlags = optionDebugFlags.mapTo(LinkedHashSet()) { it -> it + "TODO" }

        /*
         * Build spec loading and execution creation
         */

        val buildRoot = Paths.get(optionBuildRoot)

        if (!Files.exists(buildRoot) || !Files.isDirectory(buildRoot)) {
            logger.error { "The given build root does not exist or is not a directory: $optionBuildRoot" }
            return
        }

        val buildSpecFile = buildRoot.resolve("mosbuild.json")
        val sourceRoot = buildRoot.resolve("src")

        val messagePipe = ThreadLocalBufferingMessagePipe()

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

        messagePipe.streamAll().forEach { message ->
            println(message.format())
        }
    }
}
