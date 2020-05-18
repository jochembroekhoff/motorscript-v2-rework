package nl.jochembroekhoff.motorscript.pluginapi

interface AggregateRegistry<T : Registry> {
    fun createInstanceFor(plugin: MOSTargetPlugin) : T
}
