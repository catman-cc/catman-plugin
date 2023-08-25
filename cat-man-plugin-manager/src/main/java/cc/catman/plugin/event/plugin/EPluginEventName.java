package cc.catman.plugin.event.plugin;

public enum EPluginEventName {
    FINDER,  // 获取到插件信息
    PARSER,   // 解析插件
    LOAD,    // 加载插件
    START,   // 启动插件
    DISABLE, // 禁用插件
    ENABLE,  // 启用插件
    UNLOAD,  // 卸载插件
    FAIL, 处理中出现了失败
}
