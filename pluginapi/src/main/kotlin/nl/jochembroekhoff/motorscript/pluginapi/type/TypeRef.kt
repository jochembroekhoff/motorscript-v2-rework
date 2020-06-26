package nl.jochembroekhoff.motorscript.pluginapi.type

import nl.jochembroekhoff.motorscript.common.ref.NSID
import kotlin.reflect.KClass

sealed class TypeRef {
    data class Dynamic(val nsid: NSID) : TypeRef()
    data class Explicit(val klass: KClass<out MOSBasicType>) : TypeRef()
}
