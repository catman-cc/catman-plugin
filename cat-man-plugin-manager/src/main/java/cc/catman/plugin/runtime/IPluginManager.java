package cc.catman.plugin.runtime;

import cc.catman.plugin.describe.PluginDescribe;

import java.util.Collections;
import java.util.List;

public interface IPluginManager {
    IPluginConfiguration getPluginConfiguration();

    IPluginManager createNew(List<PluginDescribe> systemPluginDescribes,List<PluginDescribe> pluginDescribes);

    Class<?> deepFindClass(String name,int deep);

    // 插件自定义加载策略
    default List<String> getOrderlyClassLoadingStrategy(){
        return Collections.emptyList();
    }

    void setOrderlyClassLoadingStrategy(List<String> strategies);

    void start();

}
