package nl.jochembroekhoff.motorscript.common.extensions

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Supplier

inline fun <T> ExecutorService.supply(crossinline supplier: () -> T): CompletableFuture<T> {
    return CompletableFuture.supplyAsync(Supplier { supplier() }, this)
}
