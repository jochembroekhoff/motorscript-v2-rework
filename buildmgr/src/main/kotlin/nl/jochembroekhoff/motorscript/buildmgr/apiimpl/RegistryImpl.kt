package nl.jochembroekhoff.motorscript.buildmgr.apiimpl

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
import nl.jochembroekhoff.motorscript.pluginapi.registration.MutableRegistry
import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType

class RegistryImpl : MutableRegistry {

    companion object : KLogging()

    private val tags: MutableMap<NSID, MOSTargetPlugin> = HashMap()
    private val types: MutableMap<NSID, Pair<MOSTargetPlugin, MOSBasicType>> = HashMap()

    override fun addTagRegistration(nsid: NSID, responsible: MOSTargetPlugin) {
        logger.trace { "Register tag $nsid by ${responsible.name}" }
        // TODO: Reject if already present
        tags[nsid] = responsible
    }

    override fun addTypeRegistration(nsid: NSID, type: MOSBasicType, responsible: MOSTargetPlugin) {
        logger.trace { "Register type $nsid by ${responsible.name} ($type)" }
        // TODO: Reject if already present
        types[nsid] = responsible to type
    }

    override fun lookupType(nsid: NSID): Result<MOSBasicType, Unit> {
        return when (val lookup = types[nsid]) {
            null -> Error(Unit)
            else -> Ok(lookup.second)
        }
    }
}
