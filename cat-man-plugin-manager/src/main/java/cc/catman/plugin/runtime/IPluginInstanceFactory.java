package cc.catman.plugin.runtime;

import cc.catman.plugin.core.describe.PluginParseInfo;

/**
 * 插件实例化工程
 */
public interface IPluginInstanceFactory {

    IPluginInstance create(IPluginManager pluginManager, PluginParseInfo pluginParseInfo);
}
