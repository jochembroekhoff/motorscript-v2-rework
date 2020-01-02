package nl.jochembroekhoff.motorscript.common.messages

import java.io.PrintWriter
import java.io.StringWriter

open class ExceptionAttachment(private val exception: Exception) : MessageAttachment {
    override fun toMessageString(): String {
        StringWriter().use { sw ->
            PrintWriter(sw).use { pw ->
                pw.print("caused by ")
                exception.printStackTrace(pw)
            }
            return sw.toString()
        }
    }
}
