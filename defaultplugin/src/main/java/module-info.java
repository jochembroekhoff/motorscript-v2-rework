module motorscript.defaultplugin {
    requires kotlin.logging;
    requires kotlin.stdlib;

    requires motorscript.common;
    requires motorscript.pluginapi;

    provides nl.jochembroekhoff.motorscript.pluginapi.MOSTargetPlugin
        with nl.jochembroekhoff.motorscript.defaultplugin.impl.je.JE16;
}
