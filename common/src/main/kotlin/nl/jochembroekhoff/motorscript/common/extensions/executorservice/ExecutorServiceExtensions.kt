package nl.jochembroekhoff.motorscript.common.extensions.executorservice

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

/**
 * Supply a task to [this] executor. Will be passed to the [ExecutorService] using [CompletableFuture.supplyAsync].
 */
inline fun <T> ExecutorService.supply(crossinline supplier: () -> T): CompletableFuture<T> {
    return CompletableFuture.supplyAsync(Supplier { supplier() }, this)
}
