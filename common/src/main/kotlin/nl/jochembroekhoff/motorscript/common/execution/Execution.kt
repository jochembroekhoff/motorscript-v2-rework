package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.buildspec.BuildSpec
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe
import java.nio.file.Path

data class Execution(
    val buildSpec: BuildSpec,
    val sourceRoot: Path,
    val outputRoot: Path,
    val messagePipe: MessagePipe,
    val properties: Map<String, String>
)
