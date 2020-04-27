module motorscript.gen {
    exports nl.jochembroekhoff.motorscript.gen;

    requires kotlin.stdlib;
    requires kotlin.logging;

    requires motorscript.common;
    requires motorscript.def;
    requires motorscript.ir;

    requires org.jgrapht.core;
    requires org.apache.commons.codec;
}
