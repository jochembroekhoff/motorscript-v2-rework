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
    companion object NSIDSerializer : KSerializer<NSID> {
        override val descriptor = PrimitiveDescriptor("NSID", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: NSID) {
            encoder.encodeString("${value.namespace}:${value.name.joinToString("/")}")
        }

        override fun deserialize(decoder: Decoder): NSID {
            val content = decoder.decodeString()
            val splitColon = content.split(':', limit = 2)
            if (splitColon.size != 2) {
                throw SerializationException("Expected two NSID parts, namespace and name separated by colon.")
            }
            return NSID(splitColon[0], splitColon[1].split('/', '\\'))
        }
    }
}
