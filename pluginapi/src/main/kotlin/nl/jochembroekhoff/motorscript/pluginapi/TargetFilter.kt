package nl.jochembroekhoff.motorscript.pluginapi

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TargetFilter(val platform: String, val version: String)
