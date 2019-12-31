module motorscript.cli {
    exports nl.jochembroekhoff.motorscript.cli;

    requires kotlin.stdlib;
    requires kotlinx.cli.jvm;
    requires kotlin.logging;

    requires motorscript.buildmgr;
    requires motorscript.common;
}
