package nl.jochembroekhoff.motorscript.defaultplugin.impl.je

import nl.jochembroekhoff.motorscript.defaultplugin.impl.common.DefaultCommon

abstract class JECommon(version: String) : DefaultCommon() {
    override val description =
        "Default MotorScript plugin, provides basic support for Minecraft: Java Edition version $version"
}
