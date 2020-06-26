package nl.jochembroekhoff.motorscript.check

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.extensions.sequences.filterErrorValue
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.common.result.then
import nl.jochembroekhoff.motorscript.def.DefContainer
import nl.jochembroekhoff.motorscript.ir.container.IRDefEntryMeta
import nl.jochembroekhoff.motorscript.ir.expression.IRFullRef
import nl.jochembroekhoff.motorscript.ir.expression.IRInvoke
import nl.jochembroekhoff.motorscript.ir.expression.IRPartialRef
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Slot
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckError
import nl.jochembroekhoff.motorscript.pluginapi.check.CommonCheckMessages
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckState
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckUsageState
import nl.jochembroekhoff.motorscript.pluginapi.registration.Registry
import nl.jochembroekhoff.motorscript.pluginapi.type.CapabilityInvoke
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType
import nl.jochembroekhoff.motorscript.pluginapi.type.checkCapability
import org.jgrapht.Graphs

class CheckExecutionUnit(
    private val input: List<DefContainer<PackEntry, IRDefEntryMeta>>,
    private val registry: Registry
) : ExecutionUnit<List<DefContainer<PackEntry, IRDefEntryMeta>>>() {

    companion object : KLogging()

    override fun execute(): Result<List<DefContainer<PackEntry, IRDefEntryMeta>>, Any?> {
        logger.debug { "Executing :check" }

        val futs = input.map { defContainer ->
            ectx.executor.supply { checkContainer(defContainer) }
        }.toTypedArray()

        return gatherSafe(*futs)
            .then {
                if (it.any(Boolean::not)) {
                    // TODO: Return useful error
                    Error<List<DefContainer<PackEntry, IRDefEntryMeta>>, Any?>(Unit)
                } else {
                    // TODO: Return useful data
                    Ok<List<DefContainer<PackEntry, IRDefEntryMeta>>, Any?>(input)
                }
            }
    }

    /**
     * Recursively check dependencies of a given [IRVertex], specified as [vCurrent].
     */
    private fun checkDependenciesOf(meta: IRDefEntryMeta, vCurrent: IRVertex): Result<CheckState, List<CheckError>> {
        val dependencies = meta.g.dependenciesOf(vCurrent).toList()

        val dependencyCheckResults = dependencies
            .map { (_, v) ->
                checkDependenciesOf(meta, v)
            }
            .toList()

        if (dependencyCheckResults.any { it is Error }) {
            val combinedErrors = dependencyCheckResults.asSequence()
                .filterErrorValue() // Note: this drops possible additional error data
                .flatMap { it.asSequence() }
                .toList()
            return Error(combinedErrors)
        }

        val slots = SlotMapping().apply {
            dependencies.forEach { (e, v) -> add(e, v) }
        }

        if (vCurrent !is IRExpressionVertex) {
            return Error(listOf())
        }

        // TODO: Move this to special handler classes/methods
        return when (vCurrent) {
            is IRInvoke -> {
                val (targetE, targetV) = slots.byCategory(Slot.Category.TARGET).all().single()
                val argsPositional = slots.byCategory(Slot.Category.ARG_POSITIONAL).byIndex()
                val argsNamed = slots.byCategory(Slot.Category.ARG_NAMED).byName()

                // TODO: Put this in some kind of Resolver class
                val typeLookup: Result<MOSBasicType, Any> = when (targetV) {
                    is IRFullRef -> {
                        registry.lookupType(targetV.nsid).withError {
                            logger.trace { "Type lookup for ${targetV.nsid} failed" }
                        }
                    }
                    is IRPartialRef -> {
                        Error("not supported yet")
                    }
                    else -> Error("unknown ref")
                }

                typeLookup.then { type ->
                    type.checkCapability<CapabilityInvoke>()
                }.mapError {
                    if (it is String) {
                        listOf(CheckError(CommonCheckMessages.unspecifiedError.new(it)))
                    } else {
                        listOf()
                    }
                }.then { ivk ->
                    val usageState = CheckUsageState(CheckUsageState.Destination.DISCARD)
                    ivk.checkInvoke(usageState, listOf(), listOf()).mapError { listOf(it) }
                }
            }
            else -> Error(listOf(CheckError(CommonCheckMessages.unspecifiedError.new("Vertex not implemented."))))
        }
    }

    private fun checkContainer(container: DefContainer<PackEntry, IRDefEntryMeta>): Boolean {
        val allCheckResults = container.functions.map { (name, meta) ->
            logger.trace { "Checking function $name" }

            val firstStatement =
                Graphs.getOppositeVertex(meta.g, meta.g.outgoingEdgesOf(meta.entry).single(), meta.entry)

            checkDependenciesOf(meta, firstStatement).withError { checkErrors ->
                checkErrors.forEach { it.dispatchTo(ectx.execution.messagePipe) }
            }
        }.toList()

        return !allCheckResults.any { it is Error }
    }
}
