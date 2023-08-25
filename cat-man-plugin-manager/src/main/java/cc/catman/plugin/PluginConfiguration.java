package cc.catman.plugin;

import cc.catman.plugin.classloader.configuration.DefaultClassLoaderConfiguration;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.handler.DefaultPluginParserInfoHandlerContext;
import cc.catman.plugin.describe.handler.IPluginParserInfoHandlerContext;
import cc.catman.plugin.describe.parser.DefaultPluginParserContext;
import cc.catman.plugin.describe.parser.IPluginParserContext;
import cc.catman.plugin.event.*;
import cc.catman.plugin.extensionPoint.DefaultExtensionPointManagerFactory;
import cc.catman.plugin.extensionPoint.IExtensionPointManagerFactory;
import cc.catman.plugin.provider.IPluginDescribeProvider;
import cc.catman.plugin.runtime.DefaultPluginInstanceFactory;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginInstanceFactory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件的配置对象
 */
public class PluginConfiguration implements IPluginConfiguration {

    /**
     * 插件描述数据的提供者集合
     */
    @Getter
    protected List<IPluginDescribeProvider> providers=createPluginDescribeProviders();

    /**
     * 插件中类加载器的配置对象,用户可以通过重写{@link #createClassLoaderConfiguration}方法来定制自己的配置对象
     */
    @Getter
    protected IClassLoaderConfiguration classLoaderConfiguration=createClassLoaderConfiguration();

    @Getter
    protected IPluginParserContext pluginParserContext=createPluginParserContext();

    @Getter
    private IPluginParserInfoHandlerContext pluginParserInfoHandlerContext=createPluginParserInfoHandlerContext();

    @Getter
    protected IPluginInstanceFactory pluginInstanceFactory=createPluginInstanceFactory();

    @Getter
    protected IExtensionPointManagerFactory extensionPointManagerFactory=createExtensionPointManagerFactory();

    @Getter
    protected IEventBus eventBus=createEventBus();

    private IEventBus createEventBus() {
        return new DefaultEventBus();
    }

    private IClassLoaderConfiguration createClassLoaderConfiguration() {
        return new DefaultClassLoaderConfiguration();
    }

    protected List<IPluginDescribeProvider> createPluginDescribeProviders() {
        return new ArrayList<>();
    }



    @Override
    public IPluginParserContext createPluginParserContext() {
        return new DefaultPluginParserContext();
    }

    @Override
    public IPluginParserInfoHandlerContext createPluginParserInfoHandlerContext() {
        return new DefaultPluginParserInfoHandlerContext();
    }

    @Override
    public IPluginInstanceFactory createPluginInstanceFactory() {
        return new DefaultPluginInstanceFactory();
    }

    @Override
    public IExtensionPointManagerFactory createExtensionPointManagerFactory() {
        return new DefaultExtensionPointManagerFactory();
    }

    @Override
    public List<PluginDescribe> loadProviders() {
        return providers.stream().flatMap(p -> p.provider().stream()).collect(Collectors.toList());
    }
    @Override
    public PluginConfiguration addPluginDescribeProvider(IPluginDescribeProvider provider){
        if (provider.getPluginParserContext()!=null){
            provider.setPluginParserContext(getPluginParserContext());
        }
        this.providers.add(provider);
        return this;
    }

}