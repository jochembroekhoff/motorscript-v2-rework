package nl.jochembroekhoff.motorscript.buildmgr.apiimpl

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.pluginapi.AggregateRegistry
import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
import nl.jochembroekhoff.motorscript.pluginapi.TagRegistry

class AggregateTagRegistry : AggregateRegistry<TagRegistry> {

    private val tags: MutableMap<NSID, MOSTargetPlugin> = HashMap()

    inner class TagRegistryImpl(private val plugin: MOSTargetPlugin) : TagRegistry {
        override fun registerTag(nsid: NSID) {
            tags[nsid] = plugin
        }
    }

    override fun createInstanceFor(plugin: MOSTargetPlugin): TagRegistry {
        return TagRegistryImpl(plugin)
    }

}
