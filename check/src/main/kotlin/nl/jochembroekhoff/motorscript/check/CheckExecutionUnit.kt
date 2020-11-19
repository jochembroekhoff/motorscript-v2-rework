package nl.jochembroekhoff.motorscript.check

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.extensions.sequences.filterErrorValue
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.common.result.then
import nl.jochembroekhoff.motorscript.def.DefContainer
import nl.jochembroekhoff.motorscript.ir.container.IRDefEntryMeta
import nl.jochembroekhoff.motorscript.ir.expression.*
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.IRVertex
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Slot
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckError
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckState
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckUsageState
import nl.jochembroekhoff.motorscript.pluginapi.check.CommonCheckMessages
import nl.jochembroekhoff.motorscript.pluginapi.registration.Registry
import nl.jochembroekhoff.motorscript.pluginapi.type.CapabilityInvoke
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType
import nl.jochembroekhoff.motorscript.pluginapi.type.TypeRef
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
    private fun checkDependenciesOf(
        meta: IRDefEntryMeta,
        vCurrent: IRExpressionVertex
    ): Result<CheckState, List<CheckError>> {
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

        // TODO: Move this to special handler classes/methods
        return when (vCurrent) {
            is IRRef -> {
                // TODO: Actually resolve
                Ok(CheckState(TypeRef.Dynamic(NSID.of("unknown:Unknown"))))
            }
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
                        listOf(CheckError(CommonCheckMessages.unspecifiedError.new("Missing: $it")))
                    }
                }.then { ivk ->
                    val usageState = CheckUsageState(CheckUsageState.Destination.DISCARD)
                    ivk.checkInvoke(usageState, listOf(), listOf()).mapError { listOf(it) }
                }
            }
            is IRLiteral<*> -> {
                Ok(CheckState(TypeRef.ofLiteral(vCurrent)))
            }
            else -> Error(listOf(CheckError(CommonCheckMessages.unspecifiedError.new("Vertex ${vCurrent::class.simpleName} not implemented."))))
        }
    }

    private fun checkContainer(container: DefContainer<PackEntry, IRDefEntryMeta>): Boolean {
        val allCheckResults = container.functions.map { (name, meta) ->
            logger.trace { "Checking function $name" }

            val firstStatement =
                Graphs.getOppositeVertex(meta.g, meta.g.outgoingEdgesOf(meta.entry).single(), meta.entry)

            // TODO: Iterate over all statements and possible branches and check dependencies for each of them

            val directDependencies = SlotMapping().apply {
                meta.g.dependenciesOf(firstStatement).forEach { (e, v) -> add(e, v) }
            }

            // TODO: Check different dependencies based on which statement we're currently looking at

            directDependencies.byCategory(Slot.Category.SOURCE).all().map { (_, vDep) ->
                checkDependenciesOf(meta, vDep).withError { checkErrors ->
                    checkErrors.forEach { it.dispatchTo(ectx.execution.messagePipe) }
                }
            }
        }.flatten().toList()

        return !allCheckResults.any { it is Error }
    }
}
