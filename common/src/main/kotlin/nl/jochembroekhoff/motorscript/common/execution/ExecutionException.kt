package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.messages.Dispatchable
import java.lang.Exception

abstract class ExecutionException : Exception(), Dispatchable
