package cc.catman.plugin.runtime;

public enum EPluginStatus {
    INIT,
    LOAD,
    WAIT_DEPENDENCIES,
    LOAD_EXTENSION_POINTS,
    DISABLED,
    READY,
}
