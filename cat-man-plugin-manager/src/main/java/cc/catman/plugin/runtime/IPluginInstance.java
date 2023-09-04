package cc.catman.plugin.runtime;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.options.PluginOptions;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 插件实例
 */
public interface IPluginInstance {
    String getGroup();

    String getName();

    String getVersion();

    EPluginStatus getStatus();

    PluginParseInfo getPluginParseInfo();

    /**
     * 获取插件的类加载器
     */
    ClassLoader getClassLoader();

    PluginOptions getPluginOptions();

    Set<IPluginInstance> getReferencePluginInstance();

    Set<IPluginInstance> getUsedPluginInstance();

    /**
     * 获取受控的插件管理器
     */
    IPluginManager getOwnerPluginManager();

    /**
     *  需要注意的是,这里的插件管理器不能管理当前插件实例,只能管理当前插件的子插件数据
     */
    IPluginManager getPluginManager();

    /**
     * 获取扩展点管理器
     */
    IExtensionPointManager getExtensionPointManager();

    // 插件自定义加载策略
    default List<String> getOrderlyClassLoadingStrategy(){
        return Collections.emptyList();
    }

    void  setPluginParseInfo(PluginParseInfo parseInfo);

    void setPluginOptions(PluginOptions option);

    void setExtensionPointManager(IExtensionPointManager extensionPointManager);

    void setOrderlyClassLoadingStrategy(List<String> strategies);

    void  addReference(IPluginInstance instance);

    void addUsed(IPluginInstance instance);

    void  updateStatus(EPluginStatus status);

    void start();

    void stop();

    void uninstall();
}
