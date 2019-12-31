package nl.jochembroekhoff.motorscript.common.messages

interface MessagePipe {
    fun dispatch(message: Message)
}
