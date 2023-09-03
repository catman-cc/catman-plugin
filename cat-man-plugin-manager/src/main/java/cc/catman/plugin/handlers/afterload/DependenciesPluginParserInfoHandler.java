package cc.catman.plugin.handlers.afterload;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import cc.catman.plugin.runtime.EPluginStatus;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginManager;

import java.util.Collections;
import java.util.List;

/**
 * 加载处理插件的依赖
 * 因为要创建子PluginManager,所以,依赖项的加载只能放在LOAD之后
 */
public class DependenciesPluginParserInfoHandler extends AbstractPluginParserInfoHandler {
    @Override
    public List<String> lifeCycles() {
        return Collections.singletonList(ELifeCycle.AFTER_LOAD.name());
    }

    @Override
    protected boolean doSupport(PluginParseInfo parseInfo) {
        return parseInfo.getDependencies().size()>0;
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        // 获取所有依赖,然后将依赖委托给Processor去处理
        IPluginManager pluginManager = parseInfo.getPluginInstance().getPluginManager();
        IPluginConfiguration pluginConfiguration = pluginManager.getPluginConfiguration();
        IDependenciesLoadStrategyFactory dependenciesLoadStrategyFactory = pluginConfiguration.getDependenciesLoadStrategyFactory();
        IDependenciesLoadStrategy strategy = dependenciesLoadStrategyFactory.create(pluginManager);
        strategy.load(pluginManager,parseInfo.getDependencies());
        // 更新状态
        parseInfo.getPluginInstance().updateStatus(EPluginStatus.LOAD_DEPENDENCIES);
       return true;
    }
}
