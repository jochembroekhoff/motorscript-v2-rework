package nl.jochembroekhoff.motorscript.front

import nl.jochembroekhoff.motorscript.common.messages.Level
import nl.jochembroekhoff.motorscript.common.messages.MessageBase

internal object Messages {
    private const val category = "FRONT"

    val notImplemented = MessageBase(Level.WARNING, category, 1, "Feature not implemented.")
    val duplicateNamedArgument = MessageBase(Level.ERROR, category, 2, "Duplicate named argument.")
    val implicitSelectorName = MessageBase(Level.WARNING, category, 3, "Using implicit selector name property.")
    val duplicateModifier = MessageBase(Level.WARNING, category, 4, "Same modifier specified multiple times.")
    val unreachableCode = MessageBase(Level.WARNING, category, 5, "Unreachable code found.")
}
