package nl.jochembroekhoff.motorscript.gen

class ElementCounter {

    private val counters: MutableMap<String, Int> = HashMap()

    fun next(element: String): Int {
        val current = counters[element] ?: 0
        counters[element] = current + 1
        return current
    }
}
