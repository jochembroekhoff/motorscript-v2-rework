package nl.jochembroekhoff.motorscript.common.pack

import nl.jochembroekhoff.motorscript.common.extensions.path.div
import java.nio.file.Path

// TODO: Allow extension to be omitted and have fallbacks per type
data class PackEntry(
    val type: String,
    val namespace: String,
    val nameBase: List<String>,
    val name: String,
    val extension: String
) {
    fun file(source: Path): Path {
        return source / namespace / type / nameBase / "$name.$extension"
    }
}
