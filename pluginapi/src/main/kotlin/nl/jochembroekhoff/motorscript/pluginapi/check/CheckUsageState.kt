package nl.jochembroekhoff.motorscript.pluginapi.check

/**
 * Usage state holder. Keeps track of for example how the produced value will be used (not at all, variable, argument,
 * etc.).
 */
data class CheckUsageState(
    val destination: Destination
) {
    enum class Destination {
        DISCARD,
        CONTAINER,
        ARGUMENT,
    }
}
