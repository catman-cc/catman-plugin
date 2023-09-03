package cc.catman.plugin.event.extensionPoint;

public enum ExtensionPointEventName {

    FIND_EXTENSION_POINT_START,
    FIND_EXTENSION_POINT_END,
    NOT_FOUND,
    FOUND, // 找个一个扩展点定义
    INSTANCE, // 实例化一个扩展点定义
    CHANGE, // 扩展点发生变化

    WILL_STOP, // 指定的插件即将停止
}
