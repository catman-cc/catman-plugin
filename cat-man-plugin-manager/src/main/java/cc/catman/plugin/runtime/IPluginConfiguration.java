package cc.catman.plugin.runtime;

import cc.catman.plugin.PluginConfiguration;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.handler.IPluginParserInfoHandlerContext;
import cc.catman.plugin.describe.parser.IPluginParserContext;
import cc.catman.plugin.event.IEvent;
import cc.catman.plugin.event.IEventBus;
import cc.catman.plugin.event.IEventListener;
import cc.catman.plugin.event.IEventPublisher;
import cc.catman.plugin.extensionPoint.IExtensionPointManagerFactory;
import cc.catman.plugin.provider.IPluginDescribeProvider;

import java.util.List;

public interface IPluginConfiguration extends IEventBus {

    List<IPluginDescribeProvider> getProviders();

    List<PluginDescribe> loadProviders();

    IClassLoaderConfiguration getClassLoaderConfiguration();

    IPluginParserContext createPluginParserContext();

    IPluginParserContext getPluginParserContext();

    IPluginParserInfoHandlerContext createPluginParserInfoHandlerContext();
    IPluginParserInfoHandlerContext getPluginParserInfoHandlerContext();

    IPluginInstanceFactory createPluginInstanceFactory();
    IPluginInstanceFactory getPluginInstanceFactory();

    IExtensionPointManagerFactory createExtensionPointManagerFactory();
    IExtensionPointManagerFactory getExtensionPointManagerFactory();


    PluginConfiguration addPluginDescribeProvider(IPluginDescribeProvider provider);

    IEventBus getEventBus();
    default void addEventPublishers(IEventPublisher<?, ?>... eventPublishers) {
        this.getEventBus().addEventPublishers(eventPublishers);
    }

    default void addListener(IEventListener<?, ?> listener) {
        this.getEventBus().addListener(listener);
    }

    default void publish(IEvent event) {
        this.getEventBus().publish(event);
    }




}
