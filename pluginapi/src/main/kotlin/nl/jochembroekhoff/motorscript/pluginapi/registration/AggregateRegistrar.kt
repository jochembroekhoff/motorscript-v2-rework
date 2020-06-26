package nl.jochembroekhoff.motorscript.pluginapi.registration

import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin

interface AggregateRegistrar<T : Registrar> {
    fun createInstanceFor(plugin: MOSTargetPlugin) : T
}
