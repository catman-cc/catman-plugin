package cc.catman.plugin.core;

/**
 * 扩展点的接口定义,后续会定义几个方法来响应回调,如果不需要回调,这里并不强制实现该接口
 */
public interface IExtensionPoint {
    /**
     * 实例化之后
     */
    void afterCreate();

    /**
     * 销毁之前
     */
    void beforeDestroy();

    /**
     * 被禁用
     */
    void onDisable();

    /**
     * 被启用
     */
    void onEnable();
}
