package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.jochembroekhoff.motorscript.common.ref.NSID

@Serializable
open class DefContainer<TMeta, TFunction : DefEntryMeta>(val meta: TMeta) {

    @Transient
    private val allNSIDs: MutableSet<NSID> = HashSet()

    @SerialName("functions")
    private val functionMapping: MutableMap<String, MutableMap<NSID, TFunction>> = HashMap()

    init {
        // Restore allNSIDs from the function mapping after deserialization (because allNSIDs is transient)
        if (functionMapping.isNotEmpty()) {
            functionMapping.asSequence()
                .flatMap { it.value.keys.asSequence() }
                .forEach { allNSIDs.add(it) }
        }
    }

    val functions: Sequence<Pair<NSID, TFunction>>
        get() = functionMapping.asSequence()
            .flatMap { it.value.asSequence() }
            .map { it.toPair() }

    fun registerFunction(name: NSID, meta: TFunction) {
        if (!allNSIDs.add(name)) {
            TODO("Return failure when element clashes")
        }
        functionMapping.computeIfAbsent(name.namespace) { HashMap() }[name] = meta
    }
}
