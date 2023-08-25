package cc.catman.plugin.classloader.context;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.classloader.exceptions.ClassLoadRuntimeException;
import cc.catman.plugin.classloader.handler.IClassLoaderHandler;
import cc.catman.plugin.classloader.handler.Payload;
import cc.catman.plugin.classloader.strategy.ClassLoadingStrategyProcessor;
import cc.catman.plugin.classloader.strategy.EClassLoadingStrategy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

public class DefaultClassLoaderContext implements ClassLoaderContext {

    protected IClassLoaderConfiguration classLoaderConfiguration;
    @Getter
    protected IPluginInstance pluginInstance;
    /**
     * 这是默认的类加载策略
     */
    @Setter
    protected List<String> orderlyClassLoadingStrategy= Arrays.asList(
            EClassLoadingStrategy.SELF.name()
            ,EClassLoadingStrategy.PARENT.name()
            ,EClassLoadingStrategy.DEPENDENCY.name()
    );

    protected ClassLoadingStrategyProcessor classLoadingStrategyProcessor=createClassLoadingStrategyProcessor();

    private ClassLoadingStrategyProcessor createClassLoadingStrategyProcessor() {
        return new ClassLoadingStrategyProcessor();
    }

    protected ConfigurableClassLoaderEnhancer classLoaderEnhancer;

    public DefaultClassLoaderContext(IClassLoaderConfiguration classLoaderConfiguration,IPluginInstance pluginInstance) {
        this.classLoaderConfiguration = classLoaderConfiguration;
        this.pluginInstance=pluginInstance;
    }

    @Override
    public IClassLoaderConfiguration getClassLoaderConfiguration() {
        return this.classLoaderConfiguration;
    }

    @Override
    public Class<?> loadClass(String className,ClassLoader classLoader, Function<String, Class<?>> f)
            throws SecurityException, ClassNotFoundException {
        Payload payload = new Payload();
        payload.setClassName(className);
        payload.setContext(this);
        payload.setClassLoader(classLoader);
        payload.setSelfLoadFunction(f);
        payload.setPluginInstance(this.pluginInstance);

        IClassLoaderHandler chain = getClassLoaderConfiguration().getClassLoaderHandler();
        payload = chain.before(payload);
        payload.resetContinue();

        if (!payload.loadedClass()){
            // 前面的处理器已经获取了class定义
            List<String> orderStrategies= CollectionUtils.isEmpty(this.pluginInstance.getOrderlyClassLoadingStrategy())
                    ?orderlyClassLoadingStrategy
                    :this.getPluginInstance().getOrderlyClassLoadingStrategy();

            if (classLoadingStrategyProcessor.loadClass(orderStrategies,payload)){
                payload.setClazz(f.apply(payload.getClassName()));
                payload.resetContinue();
            }else {
                // 类加载失败
                throw new ClassLoadRuntimeException(payload.hasError()?payload.getError():new ClassNotFoundException(payload.getClassName()));
            }
        }

        payload = chain.after(payload);
        // 完成类加载操作
        return payload.getClazz();
    }

    @Override
    public <T extends ClassLoader> T wrapper(T classLoader, Class<?>[] constructTypes, Object[] constructArgs) {
        return classLoaderEnhancer.wrapper(classLoader,constructTypes,constructArgs);
    }


}