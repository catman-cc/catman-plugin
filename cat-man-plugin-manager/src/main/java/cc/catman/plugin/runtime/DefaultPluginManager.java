package cc.catman.plugin.runtime;

import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import cc.catman.plugin.event.plugin.EPluginEventName;
import cc.catman.plugin.event.plugin.PluginEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的插件管理器实现
 */
public class DefaultPluginManager implements IPluginManager{

    protected List<IPluginInstance> pluginInstances=new ArrayList<>();
    protected List<IPluginInstance> systemPluginInstances=new ArrayList<>();
    @Getter
    protected IPluginConfiguration pluginConfiguration;
    /**
     * 保留原始的数据,便于后面提供类似于watch的机制.
     */
    protected List<PluginDescribe> pluginDescribes;
    protected List<PluginDescribe> systemPluginDescribes;

    @Getter
    @Setter
    protected List<String> orderlyClassLoadingStrategy;

    public DefaultPluginManager(IPluginConfiguration pluginConfiguration,List<PluginDescribe> systemPluginDescribes, List<PluginDescribe> pluginDescribes) {
        this.pluginConfiguration = pluginConfiguration;
        this.systemPluginDescribes=systemPluginDescribes;
        this.pluginDescribes=pluginDescribes;
    }

//    private void init() {
//        this.pluginDescribes=getPluginConfiguration().loadProviders();
//    }

    @Override
    public IPluginManager createNew(List<PluginDescribe> systemPluginDescribes,List<PluginDescribe> pluginDescribes) {
        DefaultPluginManager defaultPluginManager=new DefaultPluginManager(this.pluginConfiguration,systemPluginDescribes,pluginDescribes);
        defaultPluginManager.setOrderlyClassLoadingStrategy(getOrderlyClassLoadingStrategy());
        return new DefaultPluginManager(this.pluginConfiguration,systemPluginDescribes,pluginDescribes);
    }

    @Override
    public Class<?> deepFindClass(String name, int deep) {
        // 从所有实例中寻找类型
        for (IPluginInstance pluginInstance : pluginInstances) {
            Class<?> aClass = pluginInstance.deepFindClass(name, deep);
            if (aClass!=null){
                return aClass;
            }
        }
        return null;
    }

    public void start(){
        start(systemPluginDescribes,systemPluginInstances);
        start(pluginDescribes,pluginInstances);
    }



    public void start(List<PluginDescribe>pluginDescribes,List<IPluginInstance> pluginInstances){
        // TODO 上报事件,插件管理器开始启动
        // 1. 获取所有的provider,生成类描述文件
        // 2. 将所有的插件描述进一步进行处理
        IPluginInstanceFactory pluginInstanceFactory=  getPluginConfiguration().getPluginInstanceFactory();
        List<PluginParseInfo> pluginParseInfos=pluginDescribes
                .stream()
                .flatMap(pd->getPluginConfiguration().getPluginParserContext().parser(pd).stream())
                .collect(Collectors.toList());
        // 4. 区分插件的处理状态,筛选出成功和失败的插件


        // 3. 所有的插件解析信息,将继续转换为插件
        List<PluginParseInfo> parsedInfos = pluginParseInfos.stream()
                .flatMap(pi -> {
                    // 装载插件实例
                    IPluginInstance instance=pluginInstanceFactory.create(this,pi);
                    if (!CollectionUtils.isEmpty(pi.getPluginDescribe().getOrderlyClassLoadingStrategy())){
                        instance.setOrderlyClassLoadingStrategy(pi.getPluginDescribe().getOrderlyClassLoadingStrategy());
                    }
                    pi.setStatus(EPluginParserStatus.WAIT_PARSE);
                    pi.setPluginInstance(instance);
                    pi.setClassLoaderConfiguration(pluginConfiguration.getClassLoaderConfiguration());
                    return getPluginConfiguration().getPluginParserInfoHandlerContext().handler(pi).stream();
                })
                .collect(Collectors.toList());

        parsedInfos.forEach(pi->{
            if (EPluginParserStatus.FAIL.equals(pi.getStatus())){
                // TODO 插件解析失败,根据配置决定如何处理
                pluginConfiguration.getEventBus().publish(PluginEvent.builder()
                        .pluginInstance(pi.getPluginInstance())
                        .pluginConfiguration(pluginConfiguration)
                        .eventName(EPluginEventName.FAIL.name())
                        .build());


            }else {
                IPluginInstance pluginInstance=pi.getPluginInstance();
                pluginInstance.setClassLoader(pi.getClassLoader());
                // 除了失败,一定是成功的
                pluginInstances.add(pi.getPluginInstance());
                // TODO 此时启动插件是否不太合理
                pi.getPluginInstance().start();
            }
        });
    }
}
