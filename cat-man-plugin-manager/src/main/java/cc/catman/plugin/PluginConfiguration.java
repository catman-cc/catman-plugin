package cc.catman.plugin;

import cc.catman.plugin.classloader.configuration.DefaultClassLoaderConfiguration;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.common.Constants;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.extensionPoint.*;
import cc.catman.plugin.handlers.afterload.DefaultDependenciesLoadStrategyFactory;
import cc.catman.plugin.handlers.afterload.DependenciesPluginParserInfoHandler;
import cc.catman.plugin.handlers.afterload.IDependenciesLoadStrategy;
import cc.catman.plugin.handlers.afterload.IDependenciesLoadStrategyFactory;
import cc.catman.plugin.handlers.classes.ClassDirURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.handlers.dir.DirURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.handlers.finished.ReadPluginParserInfoHandler;
import cc.catman.plugin.handlers.jar.JarURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.handlers.normalDependency.IgnoredNormalDependencyPluginParserInfoHandler;
import cc.catman.plugin.handlers.search.FinderPluginDescribeParserInfoHandler;
import cc.catman.plugin.handlers.parser.JSONJacksonPluginDescribeParserInfoHandler;
import cc.catman.plugin.options.PluginOptions;
import cc.catman.plugin.processor.ParsingProcessProcessorConfiguration;
import cc.catman.plugin.resources.CombineResourceBrowser;
import cc.catman.plugin.resources.DirResourceBrowser;
import cc.catman.plugin.resources.JarResourceBrowser;
import cc.catman.plugin.event.*;
import cc.catman.plugin.event.extensionPoint.DefaultExtensionPointEventListener;
import cc.catman.plugin.processor.DefaultParsingProcessProcessorFactory;
import cc.catman.plugin.processor.IParsingProcessProcessorFactory;
import cc.catman.plugin.provider.IPluginDescribeProvider;
import cc.catman.plugin.runtime.DefaultPluginInstanceFactory;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginInstanceFactory;
import cc.catman.plugin.runtime.IPluginManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
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
    protected List<IPluginDescribeProvider> providers = createPluginDescribeProviders();

    /**
     * 插件中类加载器的配置对象,用户可以通过重写{@link #createClassLoaderConfiguration}方法来定制自己的配置对象
     */
    @Getter
    protected IClassLoaderConfiguration classLoaderConfiguration = createClassLoaderConfiguration();

    @Getter
    protected ParsingProcessProcessorConfiguration parsingProcessProcessorConfiguration = createParsingProcessProcessorConfiguration();

    @Getter
    private IParsingProcessProcessorFactory parsingProcessProcessorFactory = createParsingProcessProcessorFactory();

    @Getter
    protected IPluginInstanceFactory pluginInstanceFactory = createPluginInstanceFactory();

    @Getter
    protected IExtensionPointManagerFactory extensionPointManagerFactory = createExtensionPointManagerFactory();

    @Getter
    private IDependenciesLoadStrategyFactory dependenciesLoadStrategyFactory=createDependenciesLoadStrategyFactory();
    @Getter
    protected IEventBus eventBus = createEventBus();

    /**
     * 插件支持的扩展文件名称
     */
    private List<String> supportPluginDescFileNames = createSupportPluginDescFileNames();

    private List<String> createSupportPluginDescFileNames() {
        return Collections.singletonList(Constants.PLUGIN_DESCRIBE_FILE_NAME);
    }

    private IEventBus createEventBus() {
        DefaultEventBus defaultEventBus = new DefaultEventBus();
        registryLogEventHandler(defaultEventBus);
        return defaultEventBus;
    }

    private void registryLogEventHandler(DefaultEventBus defaultEventBus) {
        defaultEventBus.addEventPublishers(new DefaultEventPublisher<IEvent,ObjectEventAck>(new DefaultIEventContext<>()){});
        defaultEventBus.addListener(EventListenerBuilder.wrapper(new DefaultExtensionPointEventListener()));
    }

    private IClassLoaderConfiguration createClassLoaderConfiguration() {
        return new DefaultClassLoaderConfiguration();
    }

    protected List<IPluginDescribeProvider> createPluginDescribeProviders() {
        return new ArrayList<>();
    }


    @Override
    public IPluginInstanceFactory createPluginInstanceFactory() {
        return new DefaultPluginInstanceFactory();
    }

    @Override
    public IExtensionPointInstanceFactory createExtensionPointInstanceFactory(IExtensionPointManager extensionPointManager) {
        return new DefaultExtensionPointInstanceFactory(extensionPointManager);
    }

    @Override
    public IExtensionPointManagerFactory createExtensionPointManagerFactory() {
        return new DefaultExtensionPointManagerFactory();
    }

    @Override
    public List<String> getSupportPluginDescFileNames() {
        return this.supportPluginDescFileNames;
    }

    @Override
    public List<PluginParseInfo> loadProviders() {
        return providers.stream().flatMap(p -> p.provider().stream()).collect(Collectors.toList());
    }

    @Override
    public PluginConfiguration addPluginDescribeProvider(IPluginDescribeProvider provider) {
        this.providers.add(provider);
        return this;
    }
    private IParsingProcessProcessorFactory createParsingProcessProcessorFactory() {
        return new DefaultParsingProcessProcessorFactory(getParsingProcessProcessorConfiguration());
    }
    private ParsingProcessProcessorConfiguration createParsingProcessProcessorConfiguration() {
        return new ParsingProcessProcessorConfiguration().addHandler(new IgnoredNormalDependencyPluginParserInfoHandler())
                .addHandler(new DirURLClassLoaderPluginParserInfoHandler())
                .addHandler(new JarURLClassLoaderPluginParserInfoHandler())
                .addHandler(new ClassDirURLClassLoaderPluginParserInfoHandler())
                .addHandler(new JSONJacksonPluginDescribeParserInfoHandler())
                .addHandler(new DependenciesPluginParserInfoHandler())
                .addHandler(new ReadPluginParserInfoHandler())
                .addHandler(new FinderPluginDescribeParserInfoHandler(new CombineResourceBrowser()
                        .addResourceBrowser(new JarResourceBrowser())
                        .addResourceBrowser(new DirResourceBrowser().addSkip(Constants.DEFAULT_NORMAL_DEPENDENCIES_LIBS_DIR))));
    }

    @Override
    public PluginOptions createPluginOptions(IPluginManager pluginManager,PluginParseInfo pluginParseInfo){
//        PluginOptions parent = pluginManager.getPluginOptions();
        return PluginOptions.of();
    }

    protected IDependenciesLoadStrategyFactory createDependenciesLoadStrategyFactory(){
        return new DefaultDependenciesLoadStrategyFactory();
    }

    @Override
    public IDependenciesLoadStrategy findDependenciesLoadStrategy(IPluginManager pluginManager) {
        return getDependenciesLoadStrategyFactory().create(pluginManager);
    }
}
