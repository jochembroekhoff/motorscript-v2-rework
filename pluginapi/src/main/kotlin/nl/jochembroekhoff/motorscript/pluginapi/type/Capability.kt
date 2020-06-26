package nl.jochembroekhoff.motorscript.pluginapi.type

import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import nl.jochembroekhoff.motorscript.common.result.Result

interface Capability : MOSBasicType

/**
 * Safe way of checking if a certain [Capability] is present on a [MOSBasicType].
 */
inline fun <reified C : Capability> MOSBasicType.checkCapability(): Result<C, Any?> {
    return when (this) {
        is C -> Ok(this)
        else -> Error(null)
    }
}
