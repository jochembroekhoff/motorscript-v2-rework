package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.jochembroekhoff.motorscript.common.ref.NSID

@Serializable
open class DefContainer<TMeta, TFunction : DefEntryMeta>(val meta: TMeta) {

    @Transient
    private val allNSIDs: MutableSet<NSID> = HashSet()
    private val functions: MutableMap<String, MutableMap<NSID, TFunction>> = HashMap()

    init {
        // Restore allNSIDs from the function mapping after deserialization (because allNSIDs is transient)
        if (functions.isNotEmpty()) {
            functions.asSequence()
                .flatMap { it.value.keys.asSequence() }
                .forEach { allNSIDs.add(it) }
        }
    }

    fun registerFunction(name: NSID, meta: TFunction) {
        if (!allNSIDs.add(name)) {
            TODO("Return failure when element clashes")
        }
        functions.computeIfAbsent(name.namespace) { HashMap() }[name] = meta
    }

    fun registerVariable(name: NSID, meta: TFunction) {

    }

    fun registerConstant(name: NSID, meta: TFunction) {

    }
}
