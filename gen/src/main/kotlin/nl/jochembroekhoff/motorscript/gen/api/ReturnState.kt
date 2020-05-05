package nl.jochembroekhoff.motorscript.gen.api

sealed class ReturnState

class ReturnsVoid : ReturnState()

data class ReturnsValue(val value: String) : ReturnState()
