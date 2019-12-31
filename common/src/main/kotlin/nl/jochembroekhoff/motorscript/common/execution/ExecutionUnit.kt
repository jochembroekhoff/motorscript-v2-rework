package nl.jochembroekhoff.motorscript.common.execution

interface ExecutionUnit {
    /**
     * Execute this unit in the given [context].
     *
     * The method should return synchronously. No tasks should be left in the executor provided by the [context] that
     * are created by this execution unit.
     *
     * @param context The [ExecutionContext] to use.
     * @return True if the execution can continue. False indicates that there was at least some error.
     */
    fun executeInContext(context: ExecutionContext): Boolean
}
