package nl.jochembroekhoff.motorscript.common.util

import nl.jochembroekhoff.motorscript.common.result.Error
import nl.jochembroekhoff.motorscript.common.result.Ok
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

                @ParameterizedTest
                @CsvSource(
                    value = [
                        "hello\\nbye, 'hello\nbye'"
                    ]
                )
                fun `multiple chars`(input: String, output: String) {
                    assertEquals(Ok<String, String>(output), StringUtil.unescape(input))
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

    @Nested
    inner class Unquote {

        @Test
        fun empty() {
            assertEquals("", StringUtil.unqote(""))
        }

        @Test
        fun `fail with single quote`() {
            assertThrows<IllegalArgumentException> { StringUtil.unqote("'") }
            assertThrows<IllegalArgumentException> { StringUtil.unqote("\"") }
        }

        @ParameterizedTest
        @ValueSource(strings = ["appel", "peer", "hello with space", "OK MotorScript!?!!?"])
        fun `single quotes`(input: String) {
            assertEquals(input, StringUtil.unqote("'$input'"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["appel", "peer", "hello with space", "OK MotorScript!?!!?"])
        fun `double quotes`(input: String) {
            assertEquals(input, StringUtil.unqote("\"$input\""))
        }
    }
}
