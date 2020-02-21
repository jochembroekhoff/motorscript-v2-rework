package nl.jochembroekhoff.motorscript.cli

import kotlinx.cli.ArgType
import java.lang.Exception

class KeyValueArgType : ArgType<Pair<String, String>>(true) {
    override val description: kotlin.String
        get() = "{ key=value }"

    override val conversion: (value: kotlin.String, name: kotlin.String) -> Pair<kotlin.String, kotlin.String>
        get() = { rawValue, name ->
            val split = rawValue.split('=', limit = 2)
            if (split.size != 2) {
                throw Exception("Option $name is expected to be in key-value form, separated by an equals sign.")
            }
            val key = split[0].trimEnd()
            val value = split[1].trimStart()
            if (key.isBlank()) {
                throw Exception("Option $name is provided with a blank key.")
            }
            Pair(key, value)
        }
}
