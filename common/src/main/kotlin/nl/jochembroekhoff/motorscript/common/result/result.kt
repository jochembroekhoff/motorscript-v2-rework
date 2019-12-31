package nl.jochembroekhoff.motorscript.common.result

sealed class Result<T, U>

data class Ok<T, U>(val value: T): Result<T, U>()

data class Error<T, U>(val value: U): Result<T, U>()
