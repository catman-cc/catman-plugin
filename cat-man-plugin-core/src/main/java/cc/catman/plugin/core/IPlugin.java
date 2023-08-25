package cc.catman.plugin.core;

/**
 * 插件的接口定义,这里并不强制实现该接口
 */
public interface IPlugin {
    void onload();
    void beforeUnload();

    /**
     * 被禁用
     */
    void onDisable();

    /**
     * 被启用
     */
    void onEnable();
}
