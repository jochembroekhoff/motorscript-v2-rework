package nl.jochembroekhoff.motorscript.pluginapi.registration

import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType

interface Registry {
    fun lookupType(nsid: NSID): Result<MOSBasicType, Unit>
}
