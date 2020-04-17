package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.Serializable

@Serializable
data class EDefFunctionMeta(
    val signature: DefSignature
) : DefEntryMeta()
