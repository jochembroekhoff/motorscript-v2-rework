package nl.jochembroekhoff.motorscript.common.execution

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import java.util.concurrent.CompletableFuture

abstract class ExecutionUnit<T> {
    companion object : KLogging()

    protected lateinit var ectx: ExecutionContext

    /**
     * Prepare this unit to be executed in the given [ectx].
     */
    fun prepareContext(ectx: ExecutionContext) {
        this.ectx = ectx
    }

    /**
     * Execute this unit.
     *
     * The method should return synchronously. No tasks created by this unit should be left executing when this method
     * returns.
     *
     * @return A result object indicating whether the whole unit executed successfully or if some part failed.
     */
    abstract fun execute(): Result<T, Any?>

    /**
     * Utility method that calls [prepareContext] and then returns the return value of [execute].
     */
    fun executeInContext(ectx: ExecutionContext): Result<T, Any?> {
        prepareContext(ectx)
        return execute()
    }

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
                    } else {
                        // TODO: Improve this unidentified execution exception. Constructing an
                        //       UnidentifiedExecutionException creates an extra stack trace to this method which is not
                        //       really accurate since this is not the place where the error happens
                        exceptions.add(UnidentifiedExecutionException(cause ?: ex))
                    }
                } catch (ex: Exception) {
                    exceptions.add(UnidentifiedExecutionException(ex))
                }
            }
            Error(exceptions)
        }
    }
}
