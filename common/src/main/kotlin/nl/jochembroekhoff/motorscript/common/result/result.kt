package nl.jochembroekhoff.motorscript.common.result

sealed class Result<R, E> {
    /**
     * Chain an operation to be executed if the current [Result] is [Ok].
     */
    inline fun <ROut> then(handler: (R) -> Result<ROut, E>): Result<ROut, E> {
        return when (this) {
            is Ok -> handler(value)
            is Error -> Error(value)
        }
    }

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
}

data class Ok<R, E>(val value: R) : Result<R, E>()

data class Error<R, E>(val value: E) : Result<R, E>()
