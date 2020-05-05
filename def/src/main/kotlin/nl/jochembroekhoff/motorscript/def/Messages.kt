package nl.jochembroekhoff.motorscript.def

import nl.jochembroekhoff.motorscript.common.messages.Level
import nl.jochembroekhoff.motorscript.common.messages.MessageBase

object Messages {
    private const val category = "DEF"

    val nameClash = MessageBase(Level.ERROR, category, 1, "Clashing names found between dependencies.")
}
