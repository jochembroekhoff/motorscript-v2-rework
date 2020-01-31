package nl.jochembroekhoff.motorscript.front.util

import io.kotlintest.seconds
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class StringUtilTest : WordSpec({
    TODO("implement")
    "String.length" should {
        "return the length of the string".config(timeout = 2.seconds) {
            "sammy".length shouldBe 5
            "sdf".length shouldBe 0
        }
    }
    /*
    test("unescape") {
        test("successes") {
            test("empty") {
                StringUtil.unescape("") shouldBe Ok<String, String>("")
            }
            test("no escapes") {
                test("hello bye") {
                    StringUtil.unescape("hello bye") shouldBe Ok<String, String>("hello bye")
                }
                test("Testing!?>?!") {
                    StringUtil.unescape("Testing!?>?!") shouldBe Ok<String, String>("Testing!?>?!")
                }
            }
            test("escaped") {

            }
        }
        test("escape failures") {
            test("unknown char 'r'") {
                StringUtil.unescape("\\r") shouldBe Error<String, String>("Cannot escape char 'r'")
            }
        }
    }
     */
})
