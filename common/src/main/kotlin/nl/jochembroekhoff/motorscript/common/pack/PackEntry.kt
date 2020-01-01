package nl.jochembroekhoff.motorscript.common.pack

// TODO: Allow extension to be omitted and have fallbacks per type
data class PackEntry(val type: String, val namespace: String, val name: List<String>, val extension: String)
