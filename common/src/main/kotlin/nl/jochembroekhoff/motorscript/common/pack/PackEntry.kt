package nl.jochembroekhoff.motorscript.common.pack

import nl.jochembroekhoff.motorscript.common.extensions.path.div
import nl.jochembroekhoff.motorscript.common.ref.NSID
import java.nio.file.Path

// TODO: Allow extension to be omitted and have fallbacks per type
data class PackEntry(
    val type: String,
    val base: NSID,
    val name: String,
    val extension: String
) {
    fun file(source: Path): Path {
        return source / base.namespace / type / base.name / "$name.$extension"
    }
}
