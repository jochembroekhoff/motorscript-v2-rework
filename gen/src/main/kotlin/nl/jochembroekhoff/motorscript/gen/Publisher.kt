package nl.jochembroekhoff.motorscript.gen

import nl.jochembroekhoff.motorscript.common.ref.NSID
import org.apache.commons.codec.binary.Base32
import java.security.MessageDigest

class Publisher {

    companion object {
        private val suffix = "|function".toByteArray()
    }

    private val digest = MessageDigest.getInstance("SHA-256")
    private val base32 = Base32()
    private val baseHashes: MutableMap<NSID, String> = HashMap()
    private val published: MutableMap<Pair<NSID, GenElement>, NSID> = HashMap()

    fun baseHash(base: NSID): String {
        return baseHashes.computeIfAbsent(base) { key ->
            digest.update(key.toInternalRepresentation().toByteArray())
            digest.update(suffix)
            base32.encodeAsString(digest.digest().copyOfRange(0, 20)).toLowerCase()
        }
    }

    fun published(base: NSID, element: GenElement): NSID {
        return published.computeIfAbsent(base to element) {
            val suffix =
                if (element.seq >= 0)
                    "${element.name}-${element.seq}"
                else
                    element.name
            NSID("zzz__mos", listOf(base.namespace, baseHash(base), suffix))
        }
    }
}
