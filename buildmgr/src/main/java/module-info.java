module motorscript.buildmgr {
    exports nl.jochembroekhoff.motorscript.buildmgr;

    requires kotlin.stdlib;
    requires kotlin.logging;
    requires kotlinx.serialization.runtime;

    requires motorscript.check;
    requires motorscript.common;
    requires motorscript.def;
    requires motorscript.discover;
    requires motorscript.front;
    requires motorscript.gen;
    requires motorscript.lexparse;
}
