package cc.catman.plugin.provider;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.describe.StandardPluginDescribe;

import java.util.List;

/**
 * 插件描述信息的提供者,其{@link #provider()}方法负责提供一个或多个{@link StandardPluginDescribe}定义.
 * 该定义信息,最终将尽可能的背转换为插件实例.
 */
public interface IPluginDescribeProvider {

    List<PluginParseInfo> provider();
}
