package nl.jochembroekhoff.motorscript.cli

import nl.jochembroekhoff.motorscript.common.messages.Message
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe
import java.util.*
import java.util.stream.Stream

class ThreadLocalBufferingMessagePipe : MessagePipe {
    private val all: MutableList<MutableList<Message>> = Collections.synchronizedList(mutableListOf<MutableList<Message>>())
    private val messages = ThreadLocal.withInitial { mutableListOf<Message>().also { all.add(it) } }!!

    override fun dispatch(message: Message) {
        messages.get().add(message)
    }

    /**
     * Stream all collected messages.
     * Note: this assumes **NO** messages are being dispatched anymore.
     */
    fun streamAll(): Stream<Message> {
        return all.stream().flatMap { it.stream() }
    }
}
