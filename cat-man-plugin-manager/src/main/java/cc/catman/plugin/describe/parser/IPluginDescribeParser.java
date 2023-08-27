package cc.catman.plugin.describe.parser;


import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;

public interface IPluginDescribeParser {

    /**
     * 判断是否支持解析当前描述对象
     * @param standardPluginDescribe 插件描述
     */
    boolean supports(StandardPluginDescribe standardPluginDescribe);

    PluginParseInfo wrapper(StandardPluginDescribe standardPluginDescribe);

    <T extends StandardPluginDescribe> T decode(PluginParseInfo parseInfo, Class<T> clazz);
}
