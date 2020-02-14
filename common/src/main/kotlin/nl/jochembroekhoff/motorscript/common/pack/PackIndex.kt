package nl.jochembroekhoff.motorscript.common.pack

import mu.KLogging
import nl.jochembroekhoff.motorscript.common.extensions.path.allNames
import nl.jochembroekhoff.motorscript.common.extensions.path.extension
import nl.jochembroekhoff.motorscript.common.extensions.path.stem
import nl.jochembroekhoff.motorscript.common.extensions.sequences.dropNull
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.streams.asSequence

class PackIndex {
    private val root: Map<NamespaceTypeCombo, Map<List<String>, PackEntry>>

    constructor() {
        root = HashMap()
    }

    internal constructor(source: Map<NamespaceTypeCombo, Map<List<String>, PackEntry>>) {
        val new = HashMap<NamespaceTypeCombo, Map<List<String>, PackEntry>>()
        source.forEach { (k, v) -> new[k] = HashMap(v) }
        root = new
    }

    fun streamType(type: String): Stream<PackEntry> {
        return root.entries.stream()
            .filter { it.key.type == type }
            .flatMap { it.value.values.stream() }
    }

    companion object : KLogging() {
        /**
         * Load a [PackIndex] from a [Path]. This [source] is assumed to be in pack format.
         */
        fun loadFrom(source: Path): PackIndex {
            val builder = PackIndexBuilder()
            Files.walk(source).use { stream ->
                stream.asSequence()
                    // Skip directories (primarily)
                    .filter { f -> Files.isRegularFile(f) }
                    // Only have paths relative to the source
                    .map(source::relativize)
                    .map { f ->
                        logger.trace { "Handling $f" }

                        val namespace = f.getName(0).toString()
                        val type = f.getName(1).toString()
                        val nameRaw = f.allNames.drop(2)
                        val name = nameRaw
                            .mapIndexed { i, p -> if (i >= nameRaw.lastIndex) p.stem else p }
                            .map { it.toString() }
                        val extension = f.extension

                        if (name.isEmpty()) {
                            return@map null
                        }

                        return@map PackEntry(type, namespace, name.dropLast(1), name.last(), extension).also {
                            logger.trace { "Pack entry: $it" }
                        }
                    }
                    .dropNull()
                    .forEach(builder::addEntry)
            }
            return builder.build()
        }
    }
}
