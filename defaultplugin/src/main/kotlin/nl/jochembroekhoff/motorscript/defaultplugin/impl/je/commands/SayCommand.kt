package nl.jochembroekhoff.motorscript.defaultplugin.impl.je.commands

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.pluginapi.check.*
import nl.jochembroekhoff.motorscript.pluginapi.type.TypeRef

class SayCommand : JECommand() {

    companion object : KLogging()

    override fun checkInvoke(
        usageState: CheckUsageState,
        positional: List<CheckElement>,
        named: List<Pair<String, CheckElement>>
    ): Result<CheckState, CheckError> {
        logger.info { "CHECKING SAY COMMAND!!!" }
        // TODO: Soft error, with partial check state
        if (named.isNotEmpty()) {
            // TODO: Better error
            return Error(CheckError(CommonCheckMessages.namedArgumentsProhibited.new()))
        }
        val nonStrings = positional.filter { TODO() }
        if (nonStrings.isNotEmpty()) {
            // TODO
            return Error(CheckError(CommonCheckMessages.unspecifiedError.new("some arguments are not string")))
        }
        return Ok(CheckState(TypeRef.Dynamic(NSID.of("prelude:Boolean"))))
    }
}
