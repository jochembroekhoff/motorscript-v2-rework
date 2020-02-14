package nl.jochembroekhoff.motorscript.common.extensions.sequences

fun <T> Sequence<T?>.dropNull(): Sequence<T> {
    return NullDroppingSequence(this)
}

/**
 * Null-dropping sequence. Based on Kotlin's internal DropWhileSequence.
 */
private class NullDroppingSequence<T>(private val sequence: Sequence<T?>) : Sequence<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val iterator = sequence.iterator()
        var end = false
        var nextKnown = false
        var nextItem: T? = null

        private fun drop() {
            nextKnown = false
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item != null) {
                    nextKnown = true
                    nextItem = item
                    return
                }
            }
            if (!nextKnown) {
                end = true
            }
        }

        override fun next(): T {
            if (!nextKnown) {
                drop()
            }

            nextKnown = false
            return nextItem!!
        }

        override fun hasNext(): Boolean {
            if (!nextKnown) {
                drop()
            }
            if (end) {
                return false
            }
            return nextKnown
        }
    }
}
