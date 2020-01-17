package nl.jochembroekhoff.motorscript.ir.graph

abstract class IRVertex {
    internal val incoming: MutableMap<IRVertex, IREdge> = HashMap()
    internal val outgoing: MutableMap<IRVertex, IREdge> = HashMap()
}
