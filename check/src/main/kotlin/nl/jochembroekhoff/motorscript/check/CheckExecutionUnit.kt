package nl.jochembroekhoff.motorscript.check

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.def.DefContainer
import nl.jochembroekhoff.motorscript.ir.container.IRDefEntryMeta

class CheckExecutionUnit(private val input: List<DefContainer<PackEntry, IRDefEntryMeta>>) : ExecutionUnit<List<DefContainer<PackEntry, IRDefEntryMeta>>>() {

    companion object : KLogging()

    override fun execute(): Result<List<DefContainer<PackEntry, IRDefEntryMeta>>, Unit> {
        logger.debug { "Executing :check" }

        val futs = input.map { defContainer ->
            ectx.executor.supply {

            }
        }.toTypedArray()

        gatherSafe(*futs)

        return Ok(input)
    }
}
