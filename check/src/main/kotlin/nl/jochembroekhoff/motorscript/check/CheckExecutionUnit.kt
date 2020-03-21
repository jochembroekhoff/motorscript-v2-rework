package nl.jochembroekhoff.motorscript.check

import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result

class CheckExecutionUnit : ExecutionUnit<Unit>() {
    override fun executeInContext(ectx: ExecutionContext): Result<Unit, Unit> {
        return Ok(Unit)
    }
}
