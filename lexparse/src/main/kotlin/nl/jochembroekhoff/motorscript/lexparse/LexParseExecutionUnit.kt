package nl.jochembroekhoff.motorscript.lexparse
import mu.KLogging
import nl.jochembroekhoff.motorscript.common.execution.ExecutionContext
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.supply
import nl.jochembroekhoff.motorscript.common.pack.PackIndex

class LexParseExecutionUnit(val sourceIndex: PackIndex) : ExecutionUnit {

    companion object : KLogging()

    override fun executeInContext(context: ExecutionContext): Boolean {
        context.executor.supply {

        }
    }

}
