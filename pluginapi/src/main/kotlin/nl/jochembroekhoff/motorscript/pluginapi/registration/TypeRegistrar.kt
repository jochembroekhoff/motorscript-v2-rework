package nl.jochembroekhoff.motorscript.pluginapi.registration

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType

interface TypeRegistrar : Registrar {
    fun registerType(nsid: NSID, type: MOSBasicType)
}
