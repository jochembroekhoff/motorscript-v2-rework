package nl.jochembroekhoff.motorscript.common.extensions

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException

inline fun <T> T?.require(message: () -> String): T {
    return this ?: throw InternalAssertionExecutionException(message())
}
