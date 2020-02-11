package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.messages.Attachable
import nl.jochembroekhoff.motorscript.common.messages.CommonMessages
import nl.jochembroekhoff.motorscript.common.messages.ExceptionAttachment
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe

class UnidentifiedExecutionException(
    override val cause: Throwable,
    val description: String = "",
    val attachables: List<Attachable> = listOf()
) : ExecutionException() {
    override fun dispatchTo(mp: MessagePipe) {
        mp.dispatch(CommonMessages.unidentifiedException.new(description, attachables + ExceptionAttachment(this)))
    }
}
