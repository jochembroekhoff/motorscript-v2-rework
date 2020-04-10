package nl.jochembroekhoff.motorscript.front.visitres

import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex

data class BlockVisitorResult(
    val entry: IRStatementVertex,
    val exits: Set<Exit>
)
