package nl.jochembroekhoff.motorscript.common.extensions.tuples

/**
 * Remap the first element of a [Pair].
 */
inline fun <A, B, C> Pair<A, B>.mapFirst(mapper: (A) -> C): Pair<C, B> = Pair(mapper(first), second)

/**
 * Remap the second element of a [Pair].
 */
inline fun <A, B, C> Pair<A, B>.mapSecond(mapper: (B) -> C): Pair<A, C> = Pair(first, mapper(second))
