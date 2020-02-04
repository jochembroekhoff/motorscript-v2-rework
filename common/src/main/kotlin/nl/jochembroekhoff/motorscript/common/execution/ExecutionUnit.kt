package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import java.util.concurrent.CompletableFuture

abstract class ExecutionUnit<T> {
    /**
     * Execute this unit in the given [ectx].
     *
     * The method should return synchronously. No tasks should be left in the executor provided by the [ectx] that
     * are created by this execution unit.
     *
     * @param ectx The [ExecutionContext] to use.
     * @return A result object indicating whether the whole unit executed successfully or if some part failed.
     */
    abstract fun executeInContext(ectx: ExecutionContext): Result<T, Unit>

    /**
     * Safely gather a [Result] of some [futs].
     *
     * The [Result] will be [Ok] iff all submitted futures completed successfully, then [Ok] will provide a [List] of
     * all results produced by the futures.
     *
     * The [Result] will be [Error] if any of the submitted futures completed exceptionally. All exceptions thrown by
     * the futures will be collected and filtered to only include [ExecutionException]s. This means that the [List] of
     * [ExecutionException]s in [Error] may not always reflect the order of exceptions thrown by the given futures.
     *
     * @param futs [CompletableFuture]s to gather.
     * @return A [Result], being [Ok] or [Error], determined by the aforementioned rules.
     */
    fun <U> gatherSafe(vararg futs: CompletableFuture<U>): Result<List<U>, List<ExecutionException>> {
        return try {
            CompletableFuture.allOf(*futs).get()
            Ok(futs.map { it.get() })
        } catch (_: java.util.concurrent.ExecutionException) {
            val exceptions = mutableListOf<ExecutionException>()
            futs.forEach {
                if (!it.isCompletedExceptionally) return@forEach
                try {
                    it.get()
                } catch (ex: java.util.concurrent.ExecutionException) {
                    val cause = ex.cause
                    if (cause is ExecutionException) {
                        exceptions.add(cause)
                    }
                } catch (_: Exception) {
                }
            }
            Error(exceptions)
        }
    }
}
