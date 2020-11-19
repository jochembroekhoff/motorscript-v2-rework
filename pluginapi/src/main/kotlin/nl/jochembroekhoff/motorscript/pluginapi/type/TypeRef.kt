package nl.jochembroekhoff.motorscript.pluginapi.type

import nl.jochembroekhoff.motorscript.common.execution.InternalAssertionExecutionException
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteral
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralBoolean
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralInteger
import nl.jochembroekhoff.motorscript.ir.expression.IRLiteralString
import kotlin.reflect.KClass

sealed class TypeRef {
    data class Dynamic(val nsid: NSID) : TypeRef()
    data class Explicit(val klass: KClass<out MOSBasicType>) : TypeRef()

    companion object {

        private val typeRefBoolean = NSID.of("prelude:Boolean")
        private val typeRefInteger = NSID.of("prelude:Integer")
        private val typeRefString = NSID.of("prelude:String")

        fun ofLiteral(literal: IRLiteral<*>): TypeRef {
            return when (literal) {
                is IRLiteralBoolean -> Dynamic(typeRefBoolean)
                is IRLiteralInteger -> Dynamic(typeRefInteger)
                is IRLiteralString -> Dynamic(typeRefString)
                else -> throw InternalAssertionExecutionException("Missing support for TypeRef to literal ${literal::class.simpleName}")
            }
        }
    }
}
