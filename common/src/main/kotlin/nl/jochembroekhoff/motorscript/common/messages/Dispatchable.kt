package nl.jochembroekhoff.motorscript.common.messages

interface Dispatchable {
    fun dispatchTo(mp: MessagePipe)
}
