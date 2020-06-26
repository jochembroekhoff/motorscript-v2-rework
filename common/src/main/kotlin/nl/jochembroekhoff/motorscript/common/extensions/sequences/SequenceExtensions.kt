package nl.jochembroekhoff.motorscript.common.extensions.sequences

import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result

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

inline fun <L, R> Sequence<R>.pairLeft(crossinline valueCreator: (R) -> L) = map { valueCreator(it) to it }
inline fun <L, R> Sequence<L>.pairRight(crossinline valueCreator: (L) -> R) = map { it to valueCreator(it) }

fun <R, E> Sequence<Result<R, E>>.filterOk() = filterIsInstance<Ok<R, E>>()
fun <R, E> Sequence<Result<R, E>>.filterOkValue() = filterOk().map { it.value }
fun <R, E> Sequence<Result<R, E>>.filterError() = filterIsInstance<Error<R, E>>()
fun <R, E> Sequence<Result<R, E>>.filterErrorValue() = filterError().map { it.value }
