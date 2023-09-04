package cc.catman.plugin.core;

/**
 * 插件的接口定义,这里并不强制实现该接口
 */
public interface IPlugin {

    /**
     *  插件启动后调用
     */
   default void afterStart(){}

    /**
     * 停止之前调用
     */
    default void beforeStop(){}

    /**
     * 被禁用
     */
   default void onDisable(){}

    /**
     * 被启用
     */
  default   void onEnable(){}
}
