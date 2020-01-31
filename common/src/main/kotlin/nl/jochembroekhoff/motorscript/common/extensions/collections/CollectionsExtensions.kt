package nl.jochembroekhoff.motorscript.common.extensions.collections

inline fun <T> Collection<T>.whenNotEmpty(handler: (Collection<T>) -> Unit) {
    if (this.isNotEmpty()) {
        handler(this)
    }
}
