package cc.catman.plugin.describe.parser;


import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;

public interface IPluginDescribeParser {

    /**
     * 判断是否支持解析当前描述对象
     * @param pluginDescribe 插件描述
     */
    boolean supports(PluginDescribe pluginDescribe);

    PluginParseInfo wrapper(PluginDescribe pluginDescribe);

    <T extends PluginDescribe> T decode(PluginParseInfo parseInfo,Class<T> clazz);
}
