package nl.jochembroekhoff.motorscript.ir.expression

class IRLiteralInteger(override val value: Int) : IRLiteral<Int>(value.toString())
