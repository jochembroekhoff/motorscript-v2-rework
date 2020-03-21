package nl.jochembroekhoff.motorscript.ir.refs

import nl.jochembroekhoff.motorscript.common.ref.NSID

class ReferenceContext {

    private val parent: ReferenceContext?
    private val mapping: MutableMap<String, String>
    private val depth: Int

    val localReferenceBase: NSID

    constructor(localReferenceBase: NSID) {
        parent = null
        depth = 0
        mapping = HashMap()
        this.localReferenceBase = localReferenceBase
    }

    /**
     * Constructor for usage by [next].
     */
    private constructor(parent: ReferenceContext?, mapping: MutableMap<String, String>, depth: Int, localReferenceBase: NSID) {
        this.parent = parent
        this.mapping = mapping
        this.depth = depth
        this.localReferenceBase = localReferenceBase
    }

    /**
     * Constructor for usage by [nested].
     */
    private constructor(parent: ReferenceContext) {
        this.parent = parent
        mapping = HashMap()
        depth = parent.depth + 1
        localReferenceBase = parent.localReferenceBase
    }

    fun next(): ReferenceContext {
        return ReferenceContext(parent, mapping, depth + 1, localReferenceBase)
    }

    fun nested(): ReferenceContext {
        return ReferenceContext(this)
    }

    fun resolve(symbol: String): String? {
        return mapping[symbol] ?: return parent?.resolve(symbol)
    }
}
