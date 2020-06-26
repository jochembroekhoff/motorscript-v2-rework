package nl.jochembroekhoff.motorscript.buildmgr.externalimpl

import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.def.DefSignature
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckElement
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckError
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckState
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckUsageState
import nl.jochembroekhoff.motorscript.pluginapi.type.CapabilityInvoke
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType

class ExternalFunction(val signature: DefSignature) : MOSBasicType, CapabilityInvoke {
    override fun checkInvoke(
        usageState: CheckUsageState,
        positional: List<CheckElement>,
        named: List<Pair<String, CheckElement>>
    ): Result<CheckState, CheckError> {
        TODO("Not yet implemented")
    }
}
