package nl.jochembroekhoff.motorscript.def

import kotlinx.serialization.Serializable

@Serializable
data class EDefContainerMeta(
    val name: String,
    val version: String,
    val description: String = ""
)
