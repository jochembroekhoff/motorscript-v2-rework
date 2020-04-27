package nl.jochembroekhoff.motorscript.gen.impl

sealed class ReturnState

class ReturnsVoid : ReturnState()

data class ReturnsValue(val value: String) : ReturnState()
