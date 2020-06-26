package nl.jochembroekhoff.motorscript.buildmgr.apiimpl

import nl.jochembroekhoff.motorscript.pluginapi.type.MOSBasicType
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

object PluginValidation {
    fun validateTypeClass(klass: KClass<out MOSBasicType>): Boolean {
        return klass.constructors.singleOrNull { it.parameters.all(KParameter::isOptional) } != null
    }
}
