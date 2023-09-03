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
        for (IPluginInstance instance : pluginManager.getPluginInstances()) {
            try {
                Class<?> aClass = instance.getClassLoader().loadClass(payload.getClassName());
                payload.setClazz(aClass);
               return payload.loadedClass();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return payload.loadedClass();
    }
}
