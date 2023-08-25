package cc.catman.plugin.classloader.context;

import java.util.function.Function;

import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;

/**
 * 类加载上下文,关于类加载相关的操作在此处完成
 */
public interface ClassLoaderContext {
    /**
     * 获取当前上下文中的类加载配置项
     */
    IClassLoaderConfiguration getClassLoaderConfiguration();

    IPluginInstance getPluginInstance();

    Class<?> loadClass(String className, ClassLoader loader,Function<String, Class<?>> load)
            throws ClassNotFoundException, SecurityException;

    <T extends ClassLoader> T wrapper(T classLoader,Class<?>[] constructTypes,Object[] constructArgs);
}
