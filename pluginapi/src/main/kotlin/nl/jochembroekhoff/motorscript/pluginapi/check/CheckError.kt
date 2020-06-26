package nl.jochembroekhoff.motorscript.pluginapi.check

import nl.jochembroekhoff.motorscript.common.messages.Dispatchable
import nl.jochembroekhoff.motorscript.common.messages.Message
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe

data class CheckError(
    val message: Message
) : Dispatchable {
    override fun dispatchTo(mp: MessagePipe) {
        mp.dispatch(message)
    }
}
