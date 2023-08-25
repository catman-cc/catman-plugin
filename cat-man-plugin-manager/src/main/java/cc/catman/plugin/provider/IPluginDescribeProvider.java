package cc.catman.plugin.provider;

import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.parser.IPluginParserContext;

import java.util.List;

/**
 * 插件描述信息的提供者,其{@link #provider()}方法负责提供一个或多个{@link PluginDescribe}定义.
 * 该定义信息,最终将尽可能的背转换为插件实例.
 */
public interface IPluginDescribeProvider {

    List<PluginDescribe> provider();
    IPluginParserContext getPluginParserContext();

    void setPluginParserContext(IPluginParserContext pluginParserContext);
}
