package nl.jochembroekhoff.motorscript.common.extensions.collections

inline fun <T, U : Collection<T>> U.whenNotEmpty(handler: (U) -> Unit) {
    if (this.isNotEmpty()) {
        handler(this)
    }
}
