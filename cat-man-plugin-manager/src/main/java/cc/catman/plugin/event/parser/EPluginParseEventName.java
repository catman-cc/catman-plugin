package cc.catman.plugin.event.parser;

public enum EPluginParseEventName {
    START, // 开始解析插件
    HANDLED,// 插件被处理

    WARN_CAN_NOT_FOUND_HANDLE, // 无法获取插件处理器
}
