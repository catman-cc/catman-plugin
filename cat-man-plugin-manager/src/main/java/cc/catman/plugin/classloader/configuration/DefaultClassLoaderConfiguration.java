package cc.catman.plugin.classloader.configuration;

import java.util.Optional;

import cc.catman.plugin.classloader.handler.*;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;

public class DefaultClassLoaderConfiguration implements IClassLoaderConfiguration {

    private IClassLoaderHandler classLoaderHandlerChain = createClassHandlerChain();

    public DefaultClassLoaderConfiguration() {
    }

    protected IClassLoaderHandler createClassHandlerChain() {
        DefaultClassLoaderHandlerChain chain = new DefaultClassLoaderHandlerChain();
        ExcludeClassNameHandler e = ExcludeClassNameHandler.create();
        ClassLoadingStrategySortedHandlerChain classLoadingStrategySortedHandlerChain=new ClassLoadingStrategySortedHandlerChain();
        DynamicClassNameClassLoaderHandler dynamicClassNameClassLoaderHandler=new DynamicClassNameClassLoaderHandler();
        return chain
                .addHandler(e)
                .addHandler(new RedirectClassLoaderHandler())
                .addHandler(classLoadingStrategySortedHandlerChain)
                .addHandler(dynamicClassNameClassLoaderHandler);
    }

    @Override
    public IClassLoaderHandler getClassLoaderHandler() {
        return this.classLoaderHandlerChain;
    }

    @Override
    public ConfigurableClassLoaderEnhancer createClassLoaderEnhancer(IPluginInstance pluginInstance) {
        return new ConfigurableClassLoaderEnhancer(this,pluginInstance);
    }

    @Override
    public Optional<ClassLoader> redirectToSpecificClassLoader(String className) {
        return Optional.empty();
    }

}