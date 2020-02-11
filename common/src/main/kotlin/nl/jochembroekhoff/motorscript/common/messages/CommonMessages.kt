package nl.jochembroekhoff.motorscript.common.messages

object CommonMessages {
    private const val category = "COMM"

    val internalAssertionError = MessageBase(Level.ERROR, category, 1, "Internal assertion error.")
    val unidentifiedException = MessageBase(Level.ERROR, category, 2, "Unidentified exception caught.")
}
