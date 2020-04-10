package nl.jochembroekhoff.motorscript.ir.container

import nl.jochembroekhoff.motorscript.def.DefEntryMeta
import nl.jochembroekhoff.motorscript.ir.flow.misc.IREntry
import nl.jochembroekhoff.motorscript.ir.graph.IRGraph

data class IRDefEntryMeta(val g: IRGraph, val entry: IREntry) : DefEntryMeta()
