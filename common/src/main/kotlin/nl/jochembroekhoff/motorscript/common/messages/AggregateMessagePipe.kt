package nl.jochembroekhoff.motorscript.common.messages

open class AggregateMessagePipe : MessagePipe {

    private val aggregated: MutableSet<MessagePipe> = HashSet()

    override fun dispatch(message: Message) {
        aggregated.forEach { it.dispatch(message) }
    }

    fun attach(messagePipe: MessagePipe) {
        aggregated.add(messagePipe)
    }

    fun detach(messagePipe: MessagePipe): Boolean {
        return aggregated.remove(messagePipe)
    }
}
