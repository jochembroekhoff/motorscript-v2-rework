module motorscript.ir {
    exports nl.jochembroekhoff.motorscript.ir.expression;
    exports nl.jochembroekhoff.motorscript.ir.flow.misc;
    exports nl.jochembroekhoff.motorscript.ir.flow.statement;
    exports nl.jochembroekhoff.motorscript.ir.graph;
    exports nl.jochembroekhoff.motorscript.ir.refs;

    requires kotlin.stdlib;

    requires motorscript.common;

    requires transitive org.jgrapht.core;
}
