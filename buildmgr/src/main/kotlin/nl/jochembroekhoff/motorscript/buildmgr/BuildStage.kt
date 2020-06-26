package nl.jochembroekhoff.motorscript.buildmgr

enum class BuildStage {
    LOAD,
    TARGET_COMPILE,
    TARGET_WRITE,
    TARGET_DONE,
    DONE
}
