package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.messages.MessagePipe
import java.lang.Exception

abstract class ExecutionException : Exception() {
    abstract fun dispatchTo(mp: MessagePipe)
}
