package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.result.Result

interface ExecutionUnit<T> {
    /**
     * Execute this unit in the given [ectx].
     *
     * The method should return synchronously. No tasks should be left in the executor provided by the [ectx] that
     * are created by this execution unit.
     *
     * @param ectx The [ExecutionContext] to use.
     * @return A result object indicating whether the whole unit executed successfully or if some part failed.
     */
    fun executeInContext(ectx: ExecutionContext): Result<T, Unit>
}
