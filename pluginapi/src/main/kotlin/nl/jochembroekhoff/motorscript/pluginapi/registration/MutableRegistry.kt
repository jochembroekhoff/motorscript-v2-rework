package nl.jochembroekhoff.motorscript.pluginapi.registration

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType

interface MutableRegistry : Registry {
    fun addTagRegistration(nsid: NSID, responsible: MOSTargetPlugin)
    fun addTypeRegistration(nsid: NSID, type: MOSBasicType, responsible: MOSTargetPlugin)
}
