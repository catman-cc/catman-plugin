package cc.catman.plugin.runtime;

public enum EPluginStatus {
    INIT,
    LOAD_DEPENDENCIES,
    ENABLED,
    DISABLED,
    READY,
    WAIT_DEPENDENCIES_START,
    WAIT_EXTENSION_POINTS_READY,
    START,
    STOPPING,// 停止中
    STOP,
}
