package nl.jochembroekhoff.motorscript.cli

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class CustomThreadFactory : ThreadFactory {

    private val threadNumber = AtomicInteger(1)
    private val threadGroup = ThreadGroup("mosc-execution")

    override fun newThread(r: Runnable): Thread {
        val thread = Thread(threadGroup, r, "mosc-execution-" + threadNumber.getAndIncrement())

        if (thread.isDaemon) {
            thread.isDaemon = false
        }

        return thread
    }
}
