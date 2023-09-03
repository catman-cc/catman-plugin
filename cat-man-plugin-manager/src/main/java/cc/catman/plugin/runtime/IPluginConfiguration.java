package cc.catman.plugin.runtime;

import cc.catman.plugin.PluginConfiguration;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.event.IEvent;
import cc.catman.plugin.event.IEventBus;
import cc.catman.plugin.event.IEventListener;
import cc.catman.plugin.event.IEventPublisher;
import cc.catman.plugin.extensionPoint.IExtensionPointInstanceFactory;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.extensionPoint.IExtensionPointManagerFactory;
import cc.catman.plugin.handlers.afterload.IDependenciesLoadStrategy;
import cc.catman.plugin.handlers.afterload.IDependenciesLoadStrategyFactory;
import cc.catman.plugin.options.PluginOptions;
import cc.catman.plugin.processor.IParsingProcessProcessorFactory;
import cc.catman.plugin.provider.IPluginDescribeProvider;

import java.util.List;

public interface IPluginConfiguration extends IEventBus {

    List<IPluginDescribeProvider> getProviders();

    List<PluginParseInfo> loadProviders();

    IClassLoaderConfiguration getClassLoaderConfiguration();

    IParsingProcessProcessorFactory getParsingProcessProcessorFactory();

    IPluginInstanceFactory createPluginInstanceFactory();

    IPluginInstanceFactory getPluginInstanceFactory();

    IExtensionPointInstanceFactory createExtensionPointInstanceFactory(IExtensionPointManager extensionPointManager);
    IExtensionPointManagerFactory createExtensionPointManagerFactory();

    IExtensionPointManagerFactory getExtensionPointManagerFactory();

    List<String> getSupportPluginDescFileNames();

    PluginConfiguration addPluginDescribeProvider(IPluginDescribeProvider provider);



    IEventBus getEventBus();

    default void addEventPublishers(IEventPublisher<?, ?>... eventPublishers) {
        this.getEventBus().addEventPublishers(eventPublishers);
    }

    default void addListener(IEventListener<?, ?> listener) {
        this.getEventBus().addListener(listener);
    }
    default void removeListener(IEventListener<?, ?> listener){
        this.getEventBus().removeListener(listener);
    }
    default void publish(IEvent event) {
        this.getEventBus().publish(event);
    }

    PluginOptions createPluginOptions(IPluginManager pluginManager,PluginParseInfo parseInfo);
    IDependenciesLoadStrategyFactory getDependenciesLoadStrategyFactory();
    IDependenciesLoadStrategy findDependenciesLoadStrategy(IPluginManager pluginManager);
}
