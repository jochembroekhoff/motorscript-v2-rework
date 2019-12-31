package nl.jochembroekhoff.motorscript.discover.pack

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.extensions.allNames
import nl.jochembroekhoff.motorscript.common.extensions.extension
import nl.jochembroekhoff.motorscript.common.extensions.stem
import nl.jochembroekhoff.motorscript.discover.DiscoverExecutionUnit
import java.nio.file.Files
import java.nio.file.Path

object PackLoader : KLogging() {
    /**
     * Load a [PackIndex] from a [Path]. This [source] is assumed to be in pack format.
     */
    fun loadFrom(source: Path): PackIndex {
        val builder = PackIndexBuilder()
        Files.walk(source).use { stream ->
            stream
                // Skip directories (primarily)
                .filter { f -> Files.isRegularFile(f) }
                // Only have paths relative to the source
                .map(source::relativize)
                .map { f ->
                    DiscoverExecutionUnit.logger.trace { "Handling $f" }

                    val namespace = f.getName(0).toString()
                    val type = f.getName(1).toString()
                    val nameRaw = f.allNames.drop(2)
                    val name = nameRaw
                        .mapIndexed { i, p -> if (i >= nameRaw.lastIndex) p.stem else p }
                        .map { it.toString() }
                    val extension = f.extension

                    return@map PackEntry(type, namespace, name, extension).also { logger.trace { "Pack entry: $it" } }
                }
                .forEach(builder::addEntry)
        }
        return builder.build()
    }
}
