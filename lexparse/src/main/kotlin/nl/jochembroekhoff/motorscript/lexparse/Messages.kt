package nl.jochembroekhoff.motorscript.lexparse

import nl.jochembroekhoff.motorscript.common.messages.Level
import nl.jochembroekhoff.motorscript.common.messages.MessageBase

object Messages {
    private const val category = "LP"

    val syntaxError = MessageBase(Level.ERROR, category, 1, "There is a syntax error.")
}
