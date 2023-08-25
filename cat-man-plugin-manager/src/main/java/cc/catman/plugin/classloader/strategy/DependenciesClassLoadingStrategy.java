package cc.catman.plugin.classloader.strategy;

import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.runtime.IPluginManager;
import cc.catman.plugin.classloader.handler.Payload;

public class DependenciesClassLoadingStrategy implements IClassLoadingStrategy{
    @Override
    public boolean load(Payload payload) {
        // 获取当前插件实例
        IPluginInstance pluginInstance=payload.getPluginInstance();
        // 获取插件实例的插件管理器
        IPluginManager pluginManager = pluginInstance.getPluginManager();
        // 然后加载类定义,即递归向下寻找类型定义
        payload.setClazz(pluginManager.deepFindClass(payload.getClassName(), 1));
        return payload.loadedClass();
    }
}
