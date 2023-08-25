package cc.catman.plugin.runtime;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;

import java.util.Collections;
import java.util.List;

/**
 * 插件实例
 */
public interface IPluginInstance {
    /**
     * 获取插件的类加载器
     */
    ClassLoader getClassLoader();

    void setClassLoader(ClassLoader classLoader);

    PluginParseInfo getPluginParseInfo();

    /**
     * 获取受控的插件管理器
     */
    IPluginManager getOwnerPluginManager();

    /**
     *  需要注意的是,这里的插件管理器不能管理当前插件实例,只能管理当前插件的子插件数据
     */
    IPluginManager getPluginManager();

    Class<?> deepFindClass(String name,int deep);
    /**
     * 获取扩展点管理器
     */
    IExtensionPointManager getExtensionPointManager();
    void setExtensionPointManager(IExtensionPointManager extensionPointManager);

    // 插件自定义加载策略
     default List<String> getOrderlyClassLoadingStrategy(){
         return Collections.emptyList();
     }

     void setOrderlyClassLoadingStrategy(List<String> strategies);

    void start();

}