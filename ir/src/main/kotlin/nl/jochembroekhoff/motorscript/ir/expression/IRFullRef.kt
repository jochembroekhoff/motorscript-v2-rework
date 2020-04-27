package nl.jochembroekhoff.motorscript.ir.expression

import nl.jochembroekhoff.motorscript.common.ref.NSID

class IRFullRef(val nsid: NSID) : IRRef() {
    override fun contentDescription(): String {
        return "NSID: ${nsid.toInternalRepresentation()}"
    }
}
