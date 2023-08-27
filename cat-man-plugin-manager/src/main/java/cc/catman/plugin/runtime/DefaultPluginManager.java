package cc.catman.plugin.runtime;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import cc.catman.plugin.event.plugin.EPluginEventName;
import cc.catman.plugin.event.plugin.PluginEvent;
import cc.catman.plugin.operator.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的插件管理器实现
 */
public class DefaultPluginManager implements IPluginManager {
    @Getter
    @Setter
    protected GAV gav;
    @Getter
    protected List<IPluginInstance> pluginInstances = new ArrayList<>();

    @Getter
    protected IPluginConfiguration pluginConfiguration;
    /**
     * 保留原始的数据,便于后面提供类似于watch的机制.
     */
    protected List<StandardPluginDescribe> standardPluginDescribes;

    @Getter
    @Setter
    protected List<String> orderlyClassLoadingStrategy;
    @Getter
    @Setter
    private IPluginInstance ownerPluginInstance;

    public DefaultPluginManager(IPluginConfiguration pluginConfiguration, List<StandardPluginDescribe> standardPluginDescribes) {
        this.pluginConfiguration = pluginConfiguration;
        this.standardPluginDescribes = standardPluginDescribes;
    }

    @Override
    public IPluginManager createNew( List<StandardPluginDescribe> standardPluginDescribes) {
        DefaultPluginManager defaultPluginManager = new DefaultPluginManager(this.pluginConfiguration,  standardPluginDescribes);
        defaultPluginManager.setOrderlyClassLoadingStrategy(getOrderlyClassLoadingStrategy());
        return new DefaultPluginManager(this.pluginConfiguration, standardPluginDescribes);
    }

    @Override
    public Class<?> deepFindClass(String name, int deep) {
        // 从所有实例中寻找类型
        for (IPluginInstance pluginInstance : pluginInstances) {
            Class<?> aClass = pluginInstance.deepFindClass(name, deep);
            if (aClass != null) {
                return aClass;
            }
        }
        return null;
    }

    @Override
    public IPluginOperator createPluginVisitor(PluginOperatorOptions options) {
        return new DefaultPluginOperator(this,options);
    }

    @Override
    public IPluginExtensionPointOperator createPluginExtensionPointOperator(IPluginOperator pluginVisitor) {
        return new DefaultPluginExtensionPointOperator(pluginVisitor);
    }

    @Override
    public IPluginExtensionPointOperator createPluginExtensionPointOperator(PluginOperatorOptions pluginOperatorOptions) {
        return createPluginExtensionPointOperator(createPluginVisitor(pluginOperatorOptions));
    }

    public void start() {
        start(standardPluginDescribes, pluginInstances);
    }

    public void start(List<StandardPluginDescribe> standardPluginDescribes, List<IPluginInstance> pluginInstances) {
        // TODO 上报事件,插件管理器开始启动
        // 1. 获取所有的provider,生成类描述文件
        // 2. 将所有的插件描述进一步进行处理
        List<PluginParseInfo> pluginParseInfos = loadPluginParseInfos(standardPluginDescribes);


        // 3. 所有的插件解析信息,将继续转换为插件
        List<PluginParseInfo> parsedInfos = handlerPluginParseInfos(pluginParseInfos);
        // 4. 区分插件的处理状态,筛选出成功和失败的插件
        createPluginInstance(pluginInstances, parsedInfos);
    }

    protected void createPluginInstance(List<IPluginInstance> pluginInstances, List<PluginParseInfo> parsedInfos) {
        parsedInfos.forEach(pi -> {
            if (EPluginParserStatus.FAIL.equals(pi.getStatus())) {
                // TODO 插件解析失败,根据配置决定如何处理
                pluginConfiguration.getEventBus().publish(PluginEvent.builder()
                        .pluginInstance(pi.getPluginInstance())
                        .pluginConfiguration(pluginConfiguration)
                        .eventName(EPluginEventName.FAIL.name())
                        .build());
            } else if (EPluginParserStatus.WAIT_PARSE.equals(pi.getStatus())){
            }
            else {
                IPluginInstance pluginInstance = pi.getPluginInstance();
                pluginInstance.setClassLoader(pi.getClassLoader());
                // 除了失败,一定是成功的
                pluginInstances.add(pi.getPluginInstance());
                // TODO 此时启动插件是否不太合理
                pi.getPluginInstance().start();
            }
        });
    }

    protected List<PluginParseInfo> handlerPluginParseInfos(List<PluginParseInfo> pluginParseInfos) {
        IPluginInstanceFactory pluginInstanceFactory = getPluginConfiguration().getPluginInstanceFactory();
        return pluginParseInfos.stream()
                .flatMap(pi -> {
                    // 装载插件实例
                    IPluginInstance instance = pluginInstanceFactory.create(this, pi);
                    if (!CollectionUtils.isEmpty(pi.getOrderlyClassLoadingStrategy())) {
                        instance.setOrderlyClassLoadingStrategy(pi.getOrderlyClassLoadingStrategy());
                    }
                    pi.setStatus(EPluginParserStatus.WAIT_PARSE);
                    pi.setPluginInstance(instance);
                    pi.setClassLoaderConfiguration(pluginConfiguration.getClassLoaderConfiguration());
                    return getPluginConfiguration().getPluginParserInfoHandlerContext().handler(pi).stream();
                })
                .collect(Collectors.toList());
    }

    protected List<PluginParseInfo> loadPluginParseInfos(List<StandardPluginDescribe> standardPluginDescribes) {
        return standardPluginDescribes
                .stream()
                .flatMap(pd -> getPluginConfiguration().getPluginParserContext().parser(pd).stream())
                .collect(Collectors.toList());
    }


}
