package cc.catman.plugin.classloader.configuration;

import java.util.Optional;

import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;
import cc.catman.plugin.classloader.handler.DefaultClassLoaderHandlerChain;
import cc.catman.plugin.classloader.handler.ExcludeClassNameHandler;
import cc.catman.plugin.classloader.handler.IClassLoaderHandler;
import cc.catman.plugin.classloader.handler.RedirectClassLoaderHandler;

public class DefaultClassLoaderConfiguration implements IClassLoaderConfiguration {

    private IClassLoaderHandler classLoaderHandlerChain = createClassHandlerChain();

    public DefaultClassLoaderConfiguration() {
    }

    protected IClassLoaderHandler createClassHandlerChain() {
        DefaultClassLoaderHandlerChain chain = new DefaultClassLoaderHandlerChain();
        ExcludeClassNameHandler e = ExcludeClassNameHandler.create();
        return chain.addHandler(e).addHandler(new RedirectClassLoaderHandler());
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