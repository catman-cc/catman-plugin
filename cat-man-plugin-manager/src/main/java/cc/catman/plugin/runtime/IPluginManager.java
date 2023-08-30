package cc.catman.plugin.runtime;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.operator.IPluginExtensionPointOperator;
import cc.catman.plugin.operator.IPluginOperator;
import cc.catman.plugin.operator.PluginOperatorOptions;
import cc.catman.plugin.options.PluginOptions;

import java.util.Collections;
import java.util.List;

public interface IPluginManager  {
    GAV getGav();
    void setGav(GAV gav);

    IPluginConfiguration getPluginConfiguration();

    IPluginInstance getOwnerPluginInstance();

    List<IPluginInstance> getPluginInstances();

    void setOwnerPluginInstance(IPluginInstance pluginInstance);

    IPluginManager createNew( List<StandardPluginDescribe> standardPluginDescribes);

    PluginOptions getPluginOptions();
    void  setPluginOptions(PluginOptions pluginOptions);

    // 插件自定义加载策略
    default List<String> getOrderlyClassLoadingStrategy(){
        return Collections.emptyList();
    }

    void setOrderlyClassLoadingStrategy(List<String> strategies);

    void start();

    Class<?> deepFindClass(String name,int deep);

    IPluginOperator createPluginVisitor(PluginOperatorOptions pluginOperatorOptions);
    default IPluginExtensionPointOperator createPluginExtensionPointOperator(){
        return createPluginExtensionPointOperator(PluginOperatorOptions.builder().build());
    }
    IPluginExtensionPointOperator createPluginExtensionPointOperator(IPluginOperator pluginVisitor);
    IPluginExtensionPointOperator createPluginExtensionPointOperator(PluginOperatorOptions pluginOperatorOptions);

}
