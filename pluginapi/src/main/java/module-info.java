module motorscript.pluginapi {
    exports nl.jochembroekhoff.motorscript.pluginapi;
    exports nl.jochembroekhoff.motorscript.pluginapi.check;
    exports nl.jochembroekhoff.motorscript.pluginapi.registration;
    exports nl.jochembroekhoff.motorscript.pluginapi.type;

    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk8;

    requires motorscript.common;
    requires motorscript.ir;

    uses nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin;
}
