package cc.catman.plugin.handlers.afterload;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.runtime.IPluginManager;

import java.util.List;

/**
 * 依赖加载策略
 */
public interface IDependenciesLoadStrategy {

    void load(IPluginManager pluginManager, List<PluginParseInfo> pluginParseInfos);
}
