package cc.catman.plugin.classloader.configuration;

import java.util.Optional;

import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;
import cc.catman.plugin.classloader.handler.IClassLoaderHandler;

/**
 * 类加载器配置
 */
public interface IClassLoaderConfiguration {
    /**
     * 获取类名称拦截器
     */
    IClassLoaderHandler getClassLoaderHandler();

    ConfigurableClassLoaderEnhancer createClassLoaderEnhancer(IPluginInstance pluginInstance);

    Optional<ClassLoader> redirectToSpecificClassLoader(String className);
}