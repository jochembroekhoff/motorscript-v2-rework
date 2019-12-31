package nl.jochembroekhoff.motorscript.common.buildspec

import kotlinx.serialization.Serializable

@Serializable
data class BuildSpec(
    val name: String,
    val version: String,
    val description: String,
    val targets: List<Target>,
    val dependencies: List<Dependency>
)

@Serializable
data class Target(
    val platform: String,
    val version: String
) {
    fun format(): String {
        return "$platform:$version"
    }
}

@Serializable
data class Dependency(
    val name: String,
    val version: String = "*"
) {
    fun format(): String {
        return "$name:$version"
    }
}
