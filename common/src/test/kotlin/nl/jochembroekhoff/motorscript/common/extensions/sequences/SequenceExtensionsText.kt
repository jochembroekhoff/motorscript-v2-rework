package nl.jochembroekhoff.motorscript.common.extensions.sequences

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SequenceExtensionsText {
    @Test
    fun dropsAllNulls() {
        val seq = sequenceOf("a", "b", null, "c", "d", null, null, "e", null)
        assertEquals(listOf("a", "b", "c", "d", "e"), seq.dropNull().toList())
    }
}
