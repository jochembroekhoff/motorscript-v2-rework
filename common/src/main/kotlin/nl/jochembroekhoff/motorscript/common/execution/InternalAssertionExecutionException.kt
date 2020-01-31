package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.messages.Attachable
import nl.jochembroekhoff.motorscript.common.messages.CommonMessages
import nl.jochembroekhoff.motorscript.common.messages.ExceptionAttachment
import nl.jochembroekhoff.motorscript.common.messages.MessagePipe

class InternalAssertionExecutionException(val description: String = "", val attachables: List<Attachable> = listOf()) :
    ExecutionException() {
    override fun dispatchTo(mp: MessagePipe) {
        // FIXME: using list concatenation here, not efficient, should be using something more efficient
        mp.dispatch(CommonMessages.internalAssertionError.new(description, attachables + ExceptionAttachment(this)))
    }
}
