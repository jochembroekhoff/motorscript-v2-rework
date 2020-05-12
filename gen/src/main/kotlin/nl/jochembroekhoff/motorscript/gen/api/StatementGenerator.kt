package nl.jochembroekhoff.motorscript.gen.api

import nl.jochembroekhoff.motorscript.gen.GenContext

/**
 * Base class for statement generators.
 *
 * Intended to only be used internally.
 */
abstract class StatementGenerator(gctx: GenContext) : Generator(gctx)
