package nl.jochembroekhoff.motorscript.defaultplugin.impl

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
import nl.jochembroekhoff.motorscript.pluginapi.TagRegistry

class DefaultTargetPluginImpl : MOSTargetPlugin() {
    override fun init(targetPlatform: String, targetVersion: String) {
        println("DEFAULT PLUGIN INIT $targetPlatform:$targetVersion")
    }

    override fun registerTags(tagRegistry: TagRegistry) {
        tagRegistry.registerTag(NSID("minecraft", listOf("load")))
        tagRegistry.registerTag(NSID("minecraft", listOf("tick")))
    }
}
