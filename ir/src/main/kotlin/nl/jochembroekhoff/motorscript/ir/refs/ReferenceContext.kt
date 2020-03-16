package nl.jochembroekhoff.motorscript.ir.refs

class ReferenceContext {

    private val parent: ReferenceContext?
    private val mapping: MutableMap<String, String>
    private val depth: Int

    constructor() {
        parent = null
        depth = 0
        mapping = HashMap()
    }

    /**
     * Constructor for usage by [next].
     */
    private constructor(parent: ReferenceContext?, mapping: MutableMap<String, String>, depth: Int) {
        this.parent = parent
        this.mapping = mapping
        this.depth = depth
    }

    /**
     * Constructor for usage by [nested].
     */
    private constructor(parent: ReferenceContext) {
        this.parent = parent
        mapping = HashMap()
        depth = parent.depth + 1
    }

    fun next(): ReferenceContext {
        return ReferenceContext(parent, mapping, depth + 1)
    }

    fun nested(): ReferenceContext {
        return ReferenceContext(this)
    }

    fun resolve(symbol: String): String? {
        return mapping[symbol] ?: return parent?.resolve(symbol)
    }
}
