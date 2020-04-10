package nl.jochembroekhoff.motorscript.def

import nl.jochembroekhoff.motorscript.common.ref.NSID

class DefContainer<T, U : DefEntryMeta>(val meta: T) {

    private val map: MutableMap<String, MutableMap<NSID, U>> = HashMap()

    fun registerFunction(name: NSID, meta: U) {
        map.computeIfAbsent(name.namespace) { HashMap() }[name] = meta
    }

    fun registerVariable(name: NSID, meta: U) {

    }

    fun registerConstant(name: NSID, meta: U) {

    }
}
