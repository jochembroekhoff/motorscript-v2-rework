package nl.jochembroekhoff.motorscript.common.ref

import kotlinx.serialization.*

@Serializable
data class NSID(val namespace: String, val name: List<String>) {
    fun toInternalRepresentation(): String {
        return "$namespace:${name.joinToString("\\")}"
    }

    fun toGameRepresentation(): String {
        return "$namespace:${name.joinToString("/")}"
    }

    operator fun div(nameAppend: String): NSID {
        return copy(name = name + nameAppend)
    }

    override fun toString(): String {
        return "NSID(${toInternalRepresentation()})"
    }

    @Serializer(forClass = NSID::class)
    companion object : KSerializer<NSID> {
        override val descriptor = PrimitiveDescriptor("NSID", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: NSID) {
            encoder.encodeString("${value.namespace}:${value.name.joinToString("/")}")
        }

        override fun deserialize(decoder: Decoder): NSID {
            val content = decoder.decodeString()
            val decoded = of(content)
            if (decoded.name.isEmpty()) {
                throw SerializationException("Expected two NSID parts, namespace and name separated by colon.")
            }
            return decoded
        }

        fun of(input: String): NSID {
            val splitColon = input.split(':', limit = 2)
            var namespace = splitColon[0]
            val name =
                if (splitColon.size == 2) {
                    splitColon[1]
                } else {
                    namespace = ""
                    splitColon[0]
                }
            return NSID(namespace, name.split('/', '\\'))
        }
    }
}
