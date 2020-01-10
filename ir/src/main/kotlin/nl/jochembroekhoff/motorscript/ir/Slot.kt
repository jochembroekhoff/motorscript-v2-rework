package nl.jochembroekhoff.motorscript.ir

data class Slot<out T>(val key: String?, val value: T)
