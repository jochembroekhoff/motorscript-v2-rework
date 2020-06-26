package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.Serializable
import nl.jochembroekhoff.motorscript.common.ref.NSID

@Serializable
data class DefSignature(
    val params: List<DefParam>,
    val returns: NSID,
    val iterator: Boolean = false,
    val const: Boolean = false
)
