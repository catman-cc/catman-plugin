package cc.catman.plugin.classloader.cglib;

import cc.catman.plugin.classloader.cglib.policy.TagNamingPolicy;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.classloader.context.ClassLoaderContext;
import cc.catman.plugin.classloader.context.DefaultClassLoaderContext;
import lombok.Getter;
import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Enhancer;

@SuppressWarnings("unchecked")
public class ConfigurableClassLoaderEnhancer {
    @Getter
    private ConfigurableClassLoaderMethodInterceptor configurableClassLoaderMethodInterceptor;

    protected IClassLoaderConfiguration classLoaderConfiguration;
    protected ClassLoaderContext classLoaderContext;
    @Getter
    protected IPluginInstance pluginInstance;

    protected static NamingPolicy NAMING_POLICY=new TagNamingPolicy("ByCatManCgLib");

    public <T> T wrapper(ClassLoader cl) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cl.getClass());
        enhancer.setNamingPolicy(NAMING_POLICY);
       this.configurableClassLoaderMethodInterceptor= crateConfigurableClassLoaderMethodInterceptor(cl);
        enhancer.setCallback( this.configurableClassLoaderMethodInterceptor);
        return (T) enhancer.create();
    }
    public <T> T wrapper(ClassLoader cl, Class<?>[] constructorTypes,Object[] constructorValues) {
        Enhancer enhancer = new Enhancer();
        enhancer.setNamingPolicy(NAMING_POLICY);
        enhancer.setSuperclass(cl.getClass());
        enhancer.setCallback(crateConfigurableClassLoaderMethodInterceptor(cl));
        return (T) enhancer.create(constructorTypes,constructorValues);
    }


    public ConfigurableClassLoaderEnhancer(IClassLoaderConfiguration classLoaderConfiguration,IPluginInstance pluginInstance) {
        this.classLoaderConfiguration = classLoaderConfiguration;
        this.pluginInstance=pluginInstance;
        this.classLoaderContext = createClassLoaderContext(classLoaderConfiguration);
    }

    public ClassLoaderContext createClassLoaderContext(IClassLoaderConfiguration classLoaderConfiguration) {
        return new DefaultClassLoaderContext(classLoaderConfiguration,this.pluginInstance);
    }

    protected ConfigurableClassLoaderMethodInterceptor crateConfigurableClassLoaderMethodInterceptor(
            ClassLoader classLoader) {
        ConfigurableClassLoaderMethodInterceptor cclmi = null;
        try {
            cclmi = new ConfigurableClassLoaderMethodInterceptor(
                    classLoaderContext, classLoader);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return cclmi;
    }

}
