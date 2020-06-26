package nl.jochembroekhoff.motorscript.pluginapi.check

import nl.jochembroekhoff.motorscript.common.messages.Level
import nl.jochembroekhoff.motorscript.common.messages.MessageBase

object CommonCheckMessages {
    private const val category = "CHECK"

    val unspecifiedError = MessageBase(Level.ERROR, category, 1, "Unspecified error.")
    val namedArgumentsProhibited = MessageBase(Level.ERROR, category, 2, "Named arguments prohibited.")
}
