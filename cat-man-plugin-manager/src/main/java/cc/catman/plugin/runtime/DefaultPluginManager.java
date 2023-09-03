package cc.catman.plugin.runtime;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.EPluginParserStatus;
import cc.catman.plugin.operator.*;
import cc.catman.plugin.options.PluginOptions;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import cc.catman.plugin.processor.IParsingProcessProcessorFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 默认的插件管理器实现
 */
@Slf4j
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
    @Getter
    @Setter
    protected List<PluginParseInfo> standardPluginDescribes;

    @Getter
    @Setter
    protected List<String> orderlyClassLoadingStrategy;
    @Getter
    @Setter
    private IPluginInstance ownerPluginInstance;

    @Getter
    protected IParsingProcessProcessorFactory parsingProcessProcessorFactory;

    /**
     * 插件的配置信息
     */
    @Getter
    @Setter
    protected PluginOptions pluginOptions;

    public DefaultPluginManager(IPluginConfiguration pluginConfiguration, List<PluginParseInfo> standardPluginDescribes) {
        this(pluginConfiguration,standardPluginDescribes, PluginOptions.of());
    }

    public DefaultPluginManager(IPluginConfiguration pluginConfiguration, List<PluginParseInfo> standardPluginDescribes, PluginOptions pluginOptions) {
        this.pluginConfiguration = pluginConfiguration;
        this.standardPluginDescribes = standardPluginDescribes;
        this.pluginOptions = pluginOptions;
        this.parsingProcessProcessorFactory=pluginConfiguration.getParsingProcessProcessorFactory();
    }




    @Override
    public IPluginManager createNew(  IPluginInstance instance,List<PluginParseInfo> standardPluginDescribes) {
        DefaultPluginManager defaultPluginManager = new DefaultPluginManager(
                this.pluginConfiguration
                ,  standardPluginDescribes
                , getPluginConfiguration()
                .createPluginOptions(this,instance.getPluginParseInfo())
        );

        defaultPluginManager.setOrderlyClassLoadingStrategy(getOrderlyClassLoadingStrategy());
        return defaultPluginManager;
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
        // 解析
        install(standardPluginDescribes);
        // 启动插件,在启动插件时,应该先启动依赖项,然后再启动自身
        pluginInstances.forEach(IPluginInstance::start);
    }

    @Override
    public void stop() {
        stop(true);
    }
    @Override
    public void stop(boolean stopDependencies){
        // 判断插件是否可以停止
        IPluginInstance is = getOwnerPluginInstance();
        if (is.getReferencePluginInstance().stream().anyMatch(ri->{
            EPluginStatus status = ri.getStatus();
            if ( EPluginStatus.STOPPING.equals(status)||EPluginStatus.STOP.equals(status)){
                return false;
            }
            log.warn("can not stop plugin:{},because the Reference plugin:{} is not stop status"
                    ,is.getPluginParseInfo().toGAV(),ri.getPluginParseInfo().toGAV());
            return true;
        })){
            log.warn("{} stop operation was skip...",is.getPluginParseInfo().toGAV());
            return;
        }
        if (stopDependencies){
            // 停止当前插件管理器使用的插件以及/停止当前插件依赖的插件
            Set<IPluginInstance> piCopy = new HashSet<>(pluginInstances);
            piCopy.addAll(getOwnerPluginInstance().getUsedPluginInstance());
            piCopy.forEach(IPluginInstance::stop);
        }else {
            pluginInstances.forEach(IPluginInstance::stop);
        }
    }

    @Override
    public void stop(GAV gav, int deep){
        IPluginOperator pluginVisitor = createPluginVisitor(PluginOperatorOptions.builder()
                .onlyReady(false)
                .build());
        pluginVisitor.list(gav,deep).forEach(IPluginInstance::stop);
    }

    @Override
    public void stop(PluginParseInfo parseInfo, int deep){
        stop(parseInfo.toGAV(),deep);
    }
    @Override
    public void stop(IPluginInstance instance){
            instance.stop();
    }

    public List<IPluginInstance> process(List<PluginParseInfo> pluginParseInfos){
        IParsingProcessProcessor processor = getParsingProcessProcessorFactory().create(pluginParseInfos,this);
        // 启动
      return processor.process();
    }

    @Override
    public List<IPluginInstance> install(List<PluginParseInfo> pluginParseInfos) {
       return process(pluginParseInfos);
    }

    @Override
    public List<IPluginInstance> install(PluginParseInfo parseInfo) {
       return install(Collections.singletonList(parseInfo));
    }

    @Override
    public void unInstall( GAV gav,int deep){
        IPluginOperator pluginVisitor = createPluginVisitor(PluginOperatorOptions.builder()
                .onlyReady(false)
                .build());
        pluginVisitor.list(gav,deep).forEach(IPluginInstance::uninstall);
    }

    @Override
    public void replace(PluginParseInfo old, PluginParseInfo n){
        unInstall(old);
        install(n);
    }

    @Override
    public IPluginInstance registryPluginInstance(PluginParseInfo pi) {
        IPluginInstanceFactory pluginInstanceFactory = getPluginConfiguration().getPluginInstanceFactory();
        IPluginInstance instance = pluginInstanceFactory.create(this, pi);
        if (!CollectionUtils.isEmpty(pi.getOrderlyClassLoadingStrategy())) {
            instance.setOrderlyClassLoadingStrategy(pi.getOrderlyClassLoadingStrategy());
        }
        pi.setStatus(EPluginParserStatus.PROCESSING);
        pi.setPluginInstance(instance);
        pi.setClassLoaderConfiguration(pluginConfiguration.getClassLoaderConfiguration());
        // 初始化插件实例
        instance.updateStatus(EPluginStatus.INIT);
        // 除了失败,一定是成功的
        pluginInstances.add(pi.getPluginInstance());
        return instance;
    }

}
