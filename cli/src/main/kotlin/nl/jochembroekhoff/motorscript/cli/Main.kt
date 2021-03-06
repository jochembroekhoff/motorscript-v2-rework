package nl.jochembroekhoff.motorscript.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple
import mu.KLogging
import nl.jochembroekhoff.motorscript.buildmgr.BuildManager
import nl.jochembroekhoff.motorscript.buildmgr.BuildStage
import nl.jochembroekhoff.motorscript.common.messages.Level
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors

object Main : KLogging() {
    @JvmStatic
    fun main(args: Array<String>) {
        val timingOverallStart = System.nanoTime()
        var timingCompilationStart = 0L
        var timingCompilationTotal = 0L
        var timingWriteStart = 0L
        var timingWriteTotal = 0L

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

        val propMap = optionProperties.toMap()
        val debugFlags = optionDebugFlags.toSet()

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
        val outputRoot = buildRoot.resolve("out")

        val messagePipe = ThreadLocalBufferingMessagePipe()

        when (val buildSpec = BuildManager.loadBuildSpec(buildSpecFile)) {
            is Error -> {
                logger.error { "Failed to load build spec: ${buildSpec.value}" }
                return
            }
            is Ok -> {
                when (val execution =
                    BuildManager.createExecution(buildSpec.value, sourceRoot, outputRoot, messagePipe, propMap)) {
                    is Error -> {
                        logger.error { "Failed to create the execution: ${execution.value}" }
                    }
                    is Ok -> {
                        val actualNumJobs =
                            if (optionJobs <= 0) Runtime.getRuntime().availableProcessors()
                            else optionJobs

                        val executor = Executors.newFixedThreadPool(actualNumJobs, CustomThreadFactory())
                        logger.debug { "Created thread pool executor of $actualNumJobs thread(s)" }

                        BuildManager.runInExecutor(execution.value, executor) { stage ->
                            when (stage) {
                                BuildStage.LOAD -> {
                                    timingCompilationStart = System.nanoTime()
                                }
                                BuildStage.TARGET_COMPILE -> {
                                    // Start compilation timer
                                    if (timingCompilationStart != 0L) { // check for !=0 in order to include the LOAD stage
                                        timingCompilationTotal += System.nanoTime() - timingCompilationStart
                                    }
                                    timingCompilationStart = System.nanoTime()
                                }
                                BuildStage.TARGET_WRITE -> {
                                    // Stop compilation timer
                                    timingCompilationTotal += System.nanoTime() - timingCompilationStart
                                    timingCompilationStart = 0L

                                    // Start write timer
                                    timingWriteStart = System.nanoTime()
                                }
                                BuildStage.TARGET_DONE -> {
                                    // Stop write timer
                                    if (timingWriteStart != 0L) { // check for != 0 because WRITE comes after COMPILE
                                        timingWriteTotal += System.nanoTime() - timingWriteStart
                                        timingWriteStart = 0L
                                    }
                                }
                            }
                        }

                        executor.shutdown()
                    }
                }
            }
        }

        var hasErrors = false
        var hasWarnings = false

        messagePipe.streamAll().forEach { message ->
            if (!hasErrors && message.base.level == Level.ERROR) {
                hasErrors = true
            }
            if (!hasWarnings && message.base.level == Level.WARNING) {
                hasWarnings = true
            }
            println(message.format())
        }

        val timingOverallEnd = System.nanoTime()

        val elapsedOverall = (timingOverallEnd - timingOverallStart) / 1e9
        val elapsedCompilation = timingCompilationTotal / 1e9
        val elapsedWriting = timingWriteTotal / 1e9
        val elapsedMisc = elapsedOverall - elapsedCompilation - elapsedWriting

        val timingText = if (elapsedCompilation + elapsedWriting > 0) {
            String.format(
                "overall %.3f, compilation %.3f, writing %.3f, misc %.3f",
                elapsedOverall,
                elapsedCompilation,
                elapsedWriting,
                elapsedMisc
            )
        } else {
            String.format("overall %.3f", elapsedOverall)
        }

        when {
            hasErrors -> {
                System.err.println("Compilation failed (took $timingText)")
            }
            hasWarnings -> {
                System.err.println("Compilation completed with warnings (took $timingText)")
            }
            else -> {
                System.err.println("Compilation succeeded (took $timingText), output located at $outputRoot")
            }
        }
    }
}
