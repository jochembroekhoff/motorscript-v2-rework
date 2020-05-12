package nl.jochembroekhoff.motorscript.gen.impl.stmt

import nl.jochembroekhoff.motorscript.gen.GenContext
import nl.jochembroekhoff.motorscript.gen.api.StatementGenerator

class ExpressionStatementGenerator(gctx: GenContext) : StatementGenerator(gctx) {
    /*TODO:
     * - Get expression by means of dependency edge
     * - Dispatch this to the expression generator for the expression
     * - Instruct the expresion generator to discard the result of the expression (probably by using some kind of "target store" mechanism)
     */
}
