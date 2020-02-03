package nl.jochembroekhoff.motorscript.front

import nl.jochembroekhoff.motorscript.common.execution.ExecutionException
import nl.jochembroekhoff.motorscript.common.messages.Attachable
import nl.jochembroekhoff.motorscript.common.messages.ExceptionAttachment
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe

class FeatureUnimplementedExecutionException(
    val description: String = "",
    val attachables: List<Attachable> = listOf()
) : ExecutionException() {
    override fun dispatchTo(mp: MessagePipe) {
        mp.dispatch(Messages.notImplemented.new(description, attachables + ExceptionAttachment(this)))
    }
}
