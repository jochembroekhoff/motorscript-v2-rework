package nl.jochembroekhoff.motorscript.common.execution

import java.util.concurrent.ExecutorService

data class ExecutionContext(val execution: Execution, val executor: ExecutorService)
