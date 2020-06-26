package nl.jochembroekhoff.motorscript.buildmgr.apiimpl

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
import nl.jochembroekhoff.motorscript.pluginapi.registration.AggregateRegistrar
import nl.jochembroekhoff.motorscript.pluginapi.registration.MutableRegistry
import nl.jochembroekhoff.motorscript.pluginapi.registration.TypeRegistrar
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType

class AggregateTypeRegistrar(private val registry: MutableRegistry) : AggregateRegistrar<TypeRegistrar> {

    inner class TagRegistrarImpl(private val plugin: MOSTargetPlugin) : TypeRegistrar {
        override fun registerType(nsid: NSID, type: MOSBasicType) {
            registry.addTypeRegistration(nsid, type, plugin)
        }
    }

    override fun createInstanceFor(plugin: MOSTargetPlugin): TypeRegistrar {
        return TagRegistrarImpl(plugin)
    }

}
