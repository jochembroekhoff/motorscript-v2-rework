package nl.jochembroekhoff.motorscript.pluginapi.registration

import nl.jochembroekhoff.motorscript.common.ref.NSID

interface TagRegistrar : Registrar {
    fun registerTag(nsid: NSID)
}
