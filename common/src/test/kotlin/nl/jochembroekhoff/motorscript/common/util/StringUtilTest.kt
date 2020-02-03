package nl.jochembroekhoff.motorscript.common.util

import nl.jochembroekhoff.motorscript.common.util.StringUtil
import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class StringUtilTest {
    @Nested
    inner class Unescape {
        @Nested
        inner class Successes {
            @Test
            fun empty() {
                assertEquals(Ok<String, String>(""), StringUtil.unescape(""))
            }

            @Nested
            inner class NoEscapes {
                @ParameterizedTest
                @ValueSource(strings = ["hello bye", "Testing!?>?!"])
                fun `return exact same value`(input: String) {
                    assertEquals(Ok<String, String>(input), StringUtil.unescape(input))
                }
            }

            @Nested
            inner class Escaped {
                @ParameterizedTest
                @CsvSource(
                    value = [
                        "\\t, '\t'",
                        "\\n, '\n'"
                    ]
                )
                fun `single char`(input: String, output: String) {
                    assertEquals(Ok<String, String>(output), StringUtil.unescape(input))
                    assertEquals(1, (StringUtil.unescape(input) as Ok).value.length)
                }
            }
        }

        @Nested
        inner class EscapeFailures {
            @ParameterizedTest
            @ValueSource(chars = ['b', 'r'])
            fun `fail on unknown escape char`(c: Char) {
                assertEquals(Error<String, String>("Cannot escape char '$c'"), StringUtil.unescape("\\$c"))
            }
        }
    }
}
