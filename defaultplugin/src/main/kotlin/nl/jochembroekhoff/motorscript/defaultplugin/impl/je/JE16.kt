package nl.jochembroekhoff.motorscript.defaultplugin.impl.je

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.defaultplugin.impl.je.commands.SayCommand
import nl.jochembroekhoff.motorscript.pluginapi.registration.TagRegistrar
import nl.jochembroekhoff.motorscript.pluginapi.TargetFilter
import nl.jochembroekhoff.motorscript.pluginapi.registration.TypeRegistrar

@TargetFilter(platform = "java", version = "1.16.2")
class JE16 : JECommon("1.16.2") {
    override fun registerTags(tagRegistry: TagRegistrar) {
        with(tagRegistry) {
            registerTag(NSID.of("minecraft:load"))
            registerTag(NSID.of("minecraft:tick"))
        }
    }

    override fun registerTypes(typeRegistry: TypeRegistrar) {
        with(typeRegistry) {
            registerType(NSID.of("minecraft:text/say"), SayCommand())
        }
    }
}
