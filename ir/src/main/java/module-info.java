module motorscript.ir {
    exports nl.jochembroekhoff.motorscript.ir.container;
    exports nl.jochembroekhoff.motorscript.ir.debugexport;
    exports nl.jochembroekhoff.motorscript.ir.expression;
    exports nl.jochembroekhoff.motorscript.ir.flow.misc;
    exports nl.jochembroekhoff.motorscript.ir.flow.statement;
    exports nl.jochembroekhoff.motorscript.ir.graph;
    exports nl.jochembroekhoff.motorscript.ir.graph.edgemeta;
    exports nl.jochembroekhoff.motorscript.ir.refs;

    requires kotlin.stdlib;

    requires motorscript.common;
    requires motorscript.def;

    requires org.jgrapht.core;
    requires org.jgrapht.io;
}
