package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.ir.refs.ReferenceContext

class IRPartialRef(
    val path: List<String>,
    val rctx: ReferenceContext,
    val treatments: Set<Treatment> = setOf()
) : IRRef() {
    enum class Treatment {
        IGNORE_LOCAL_SCOPE,
        ONLY_BUILTIN,
        USE,
    }

    override fun contentDescription(): String {
        return "Path: ${path.joinToString("\\")} (${treatments.map { it.name }})"
    }
}
