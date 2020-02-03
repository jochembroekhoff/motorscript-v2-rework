package nl.jochembroekhoff.motorscript.ir.expression

class IRLiteralBoolean (override val value: Boolean) : IRLiteral<Boolean>(value.toString())
