package nl.jochembroekhoff.motorscript.pluginapi.type

import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckElement
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckError
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckState
import nl.jochembroekhoff.motorscript.pluginapi.check.CheckUsageState

interface CapabilityInvoke : Capability {
    fun checkInvoke(
        usageState: CheckUsageState,
        positional: List<CheckElement>,
        named: List<Pair<String, CheckElement>>
    ): Result<CheckState, CheckError>
}
