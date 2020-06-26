package nl.jochembroekhoff.motorscript.defaultplugin.impl.common

import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin

abstract class DefaultCommon : MOSTargetPlugin() {
    override val name = "defaultplugin"

    override fun init(targetPlatform: String, targetVersion: String) {
        super.init(targetPlatform, targetVersion)
        println("DEFAULT PLUGIN INIT: $targetPlatform:$targetVersion")
    }
}
