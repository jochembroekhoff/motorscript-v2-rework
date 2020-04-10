package nl.jochembroekhoff.motorscript.front.visitres

import nl.jochembroekhoff.motorscript.ir.flow.statement.IRStatementVertex

data class Exit(val v: IRStatementVertex, val willReturn: ReturnKnowledge)
