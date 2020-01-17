package nl.jochembroekhoff.motorscript.front

import nl.jochembroekhoff.motorscript.common.messages.Level
import nl.jochembroekhoff.motorscript.common.messages.MessageBase

internal object Messages {
    private const val category = "FRONT"

    val notImplemented = MessageBase(Level.WARNING, category, 1, "Feature not implemented.")
}
