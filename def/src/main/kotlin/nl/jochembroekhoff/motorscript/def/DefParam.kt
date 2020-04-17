package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.Serializable
import nl.jochembroekhoff.motorscript.common.ref.NSID

@Serializable
data class DefParam(
    val name: String,
    val type: NSID
)
