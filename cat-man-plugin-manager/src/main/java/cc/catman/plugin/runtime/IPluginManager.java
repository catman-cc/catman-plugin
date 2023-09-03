package cc.catman.plugin.runtime;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.operator.IPluginExtensionPointOperator;
import cc.catman.plugin.operator.IPluginOperator;
import cc.catman.plugin.operator.PluginOperatorOptions;
import cc.catman.plugin.options.PluginOptions;
import cc.catman.plugin.processor.IParsingProcessProcessorFactory;

import java.util.Collections;
import java.util.List;

public interface IPluginManager  {
    GAV getGav();
    void setGav(GAV gav);

    IPluginConfiguration getPluginConfiguration();

    IPluginInstance getOwnerPluginInstance();

    List<PluginParseInfo> getStandardPluginDescribes();
    void setStandardPluginDescribes(List<PluginParseInfo> standardPluginDescribes);

    IParsingProcessProcessorFactory getParsingProcessProcessorFactory();

    List<IPluginInstance> getPluginInstances();

    void setOwnerPluginInstance(IPluginInstance pluginInstance);

    IPluginManager createNew( IPluginInstance instance,List<PluginParseInfo> standardPluginDescribes);

    PluginOptions getPluginOptions();
    void  setPluginOptions(PluginOptions pluginOptions);

    // 插件自定义加载策略
    default List<String> getOrderlyClassLoadingStrategy(){
        return Collections.emptyList();
    }

    void setOrderlyClassLoadingStrategy(List<String> strategies);



    void replace(PluginParseInfo old, PluginParseInfo n);

    IPluginInstance registryPluginInstance(PluginParseInfo parseInfo);

    void start();
    void stop();

    default void stop(GAV gav){
        stop(gav,1);
    }

    void stop(boolean stopDependencies);

    void stop(GAV gav, int deep);

    default   void stop(PluginParseInfo parseInfo){
        stop(parseInfo,1);
    }

    void stop(PluginParseInfo parseInfo, int deep);

    void stop(IPluginInstance instance);

    List<IPluginInstance> process(List<PluginParseInfo> pluginParseInfos);

    List<IPluginInstance> install(List<PluginParseInfo> pluginParseInfos);

    List<IPluginInstance> install(PluginParseInfo parseInfo);

    default void unInstall(IPluginInstance instance){
        instance.uninstall();
    }

    default void unInstall(PluginParseInfo parseInfo){
        unInstall(parseInfo,1);
    }

   default void unInstall(PluginParseInfo parseInfo, int deep){
       unInstall(parseInfo,deep);
   }
    default void unInstall( GAV gav){
        unInstall(gav,1);
    }
    void unInstall( GAV gav,int deep);



    IPluginOperator createPluginVisitor(PluginOperatorOptions pluginOperatorOptions);
    default IPluginExtensionPointOperator createPluginExtensionPointOperator(){
        return createPluginExtensionPointOperator(PluginOperatorOptions.builder().build());
    }
    IPluginExtensionPointOperator createPluginExtensionPointOperator(IPluginOperator pluginVisitor);
    IPluginExtensionPointOperator createPluginExtensionPointOperator(PluginOperatorOptions pluginOperatorOptions);

}
