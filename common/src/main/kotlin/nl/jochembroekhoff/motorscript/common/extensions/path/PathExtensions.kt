package nl.jochembroekhoff.motorscript.common.extensions.path

import java.nio.file.Path

/**
 * The 'stem' of a [Path].
 * The stem is the part of the part of the file name excluding the extension. If the file name has no extension, the
 * whole file name is the stem.
 */
val Path.stem: String
    get() = fileName.toString().substringBefore('.')

/**
 * The extension of a [Path].
 * The extension is the part of the path that follows after the first dot of the file name.
 */
val Path.extension: String
    get() = fileName.toString().substringAfter('.', "")

/**
 * All 'names' of a [Path], i.e. all the parts of the path.
 * Can have length zero, for example for the root file.
 */
val Path.allNames: List<Path>
    get() = 0.until(nameCount).map(::getName)

operator fun Path.div(value: Path): Path = resolve(value)

operator fun Path.div(value: String): Path = resolve(value)

operator fun Path.div(value: List<String>): Path {
    if (value.isEmpty()) {
        return this
    }
    return resolve(Path.of(value[0], *value.subList(1, value.size).toTypedArray()))
}
