package nl.jochembroekhoff.motorscript.def

import nl.jochembroekhoff.motorscript.common.ref.NSID

data class EDefBundle(
    val containers: Map<String, EDefContainer>,
    val flatNames: Map<NSID, String>
)
