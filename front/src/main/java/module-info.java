module motorscript.front {
    exports nl.jochembroekhoff.motorscript.front;

    requires kotlin.stdlib;
    requires kotlin.logging;

    requires motorscript.common;
    requires motorscript.ir;
    requires motorscript.lexparse;

    requires org.jgrapht.core;
    requires org.jgrapht.io;
}
