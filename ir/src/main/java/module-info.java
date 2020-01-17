module motorscript.ir {
    exports nl.jochembroekhoff.motorscript.ir.graph;
    exports nl.jochembroekhoff.motorscript.ir.expression;
    exports nl.jochembroekhoff.motorscript.ir.flow.misc;
    exports nl.jochembroekhoff.motorscript.ir.flow.statement;

    requires kotlin.stdlib;

    requires transitive org.jgrapht.core;
}
