package nl.jochembroekhoff.motorscript.buildmgr.apiimpl

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
import nl.jochembroekhoff.motorscript.pluginapi.registration.AggregateRegistrar
import nl.jochembroekhoff.motorscript.pluginapi.registration.MutableRegistry
import nl.jochembroekhoff.motorscript.pluginapi.registration.TagRegistrar

class AggregateTagRegistrar(private val registry: MutableRegistry) : AggregateRegistrar<TagRegistrar> {

    inner class TagRegistrarImpl(private val plugin: MOSTargetPlugin) : TagRegistrar {
        override fun registerTag(nsid: NSID) {
            registry.addTagRegistration(nsid, plugin)
        }
    }

    override fun createInstanceFor(plugin: MOSTargetPlugin): TagRegistrar {
        return TagRegistrarImpl(plugin)
    }

}
