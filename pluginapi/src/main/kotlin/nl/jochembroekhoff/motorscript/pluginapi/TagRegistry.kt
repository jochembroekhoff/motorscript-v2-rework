package nl.jochembroekhoff.motorscript.pluginapi

import nl.jochembroekhoff.motorscript.common.ref.NSID

interface TagRegistry : Registry {
    fun registerTag(nsid: NSID)
}
