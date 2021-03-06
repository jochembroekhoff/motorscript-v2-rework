module motorscript.gen {
    exports nl.jochembroekhoff.motorscript.gen;
    exports nl.jochembroekhoff.motorscript.gen.api;

    requires kotlin.stdlib;
    requires kotlin.logging;

    requires motorscript.common;
    requires motorscript.def;
    requires motorscript.ir;
    requires motorscript.pluginapi;

    requires org.jgrapht.core;
    requires org.apache.commons.codec;
}
