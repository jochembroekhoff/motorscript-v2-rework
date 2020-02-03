module motorscript.lexparse {
    exports nl.jochembroekhoff.motorscript.lexparse;
    exports nl.jochembroekhoff.motorscript.lexparse.util;

    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk8;
    requires kotlin.logging;

    requires motorscript.common;

    requires transitive org.antlr.antlr4.runtime;
}
