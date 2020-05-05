package nl.jochembroekhoff.motorscript.common.result

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.common.messages.Attachable
import nl.jochembroekhoff.motorscript.common.messages.ErrorAttachment

sealed class Result<out R, out E> {

    /**
     * Transform the success value if this [Result] is [Ok] using the given [handler].
     */
    inline fun <H> mapOk(handler: (R) -> H): Result<H, E> {
        return when (this) {
            is Ok -> Ok(handler(value))
            is Error -> Error(value)
        }
    }

    /**
     * Transform the error value if this [Result] is [Error] using the given [handler].
     */
    inline fun <H> mapError(handler: (E) -> H): Result<R, H> {
        return when (this) {
            is Ok -> Ok(value)
            is Error -> Error(handler(value))
        }
    }

    /**
     * Run the [handler] for the success value if this [Result] is [Ok].
     * The original [Result] instance is returned and not touched.
     */
    inline fun withOk(handler: (R) -> Unit): Result<R, E> {
        if (this is Ok) {
            handler(value)
        }
        return this
    }

    /**
     * Run the [handler] for the error value if this [Result] is [Error].
     * The original [Result] instance is returned and not touched.
     */
    inline fun withError(handler: (E) -> Unit): Result<R, E> {
        if (this is Error) {
            handler(value)
        }
        return this
    }

    /**
     * Run the [handler] if this [Result] is [Ok].
     * The original [Result] instance is returned and not touched.
     */
    inline fun ifOk(handler: (Ok<R, E>) -> Unit): Result<R, E> {
        if (this is Ok) {
            handler(this)
        }
        return this
    }

    /**
     * Run the [handler] if this [Result] is [Error].
     * The original [Result] instance is returned and not touched.
     */
    inline fun ifError(handler: (Error<R, E>) -> Unit): Result<R, E> {
        if (this is Error) {
            handler(this)
        }
        return this
    }

    /**
     * Expect the [Result] to be [Ok] and unwrap its value.
     * If not [Ok] (i.e. if [Error]), an [InternalAssertionExecutionException] is thrown with containing the description
     * given in [unexpected] as error text.
     *
     * @param unexpected Brief description of how the value was unexpected, e.g. "invalid string literal".
     */
    fun expect(unexpected: String = "value not present"): R {
        when (this) {
            is Ok -> return value
            is Error -> {
                throw InternalAssertionExecutionException("Unexpected $unexpected", listOf(this))
            }
        }
    }
}

data class Ok<out R, out E>(val value: R) : Result<R, E>()

data class Error<out R, out E>(val value: E) : Result<R, E>(), Attachable {
    override fun toAttachment(): ErrorAttachment {
        return ErrorAttachment(this)
    }
}

/**
 * Chain an operation to be executed if the current [Result] is [Ok].
 */
inline fun <R, E, ROut> Result<R, E>.then(handler: (R) -> Result<ROut, E>): Result<ROut, E> {
    return when (this) {
        is Ok -> handler(value)
        is Error -> Error(value)
    }
}
