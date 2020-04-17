package nl.jochembroekhoff.motorscript.gen

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.def.DefContainer
import nl.jochembroekhoff.motorscript.ir.container.IRDefEntryMeta
import java.nio.file.Paths

class GenExecutionUnit(private val input: List<DefContainer<PackEntry, IRDefEntryMeta>>) : ExecutionUnit<Unit>() {

    companion object : KLogging()

    override fun execute(): Result<Unit, Unit> {
        logger.debug { "Executing :gen" }

        val baseOutput = Paths.get("/tmp/")

        // TODO: Also need edef containers (probz passed from :check), in order to construct generators for items provided by edefs, need signatures

        return Ok(Unit)
    }
}
