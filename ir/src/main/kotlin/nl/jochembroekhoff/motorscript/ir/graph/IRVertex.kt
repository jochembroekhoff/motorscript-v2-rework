package nl.jochembroekhoff.motorscript.ir.graph

abstract class IRVertex {
    abstract fun contentClass(): String
    open fun contentDescription(): String {
        return ""
    }
}
