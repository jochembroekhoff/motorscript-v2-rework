module motorscript.common {
    exports nl.jochembroekhoff.motorscript.common.buildspec;
    exports nl.jochembroekhoff.motorscript.common.execution;
    exports nl.jochembroekhoff.motorscript.common.extensions.executorservice;
    exports nl.jochembroekhoff.motorscript.common.extensions.path;
    exports nl.jochembroekhoff.motorscript.common.messages;
    exports nl.jochembroekhoff.motorscript.common.pack;
    exports nl.jochembroekhoff.motorscript.common.result;

    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk7;
    requires kotlinx.serialization.runtime;
    requires kotlin.logging;
}
