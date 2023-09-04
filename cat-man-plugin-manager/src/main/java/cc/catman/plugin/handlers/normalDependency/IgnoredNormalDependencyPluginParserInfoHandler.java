package cc.catman.plugin.handlers.normalDependency;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.processor.IParsingProcessProcessor;

/**
 * 一个直接忽略普通第三方依赖的处理器,如果你不需要插件系统管理你的第三方依赖,那么请添加该处理器,到处理链的顶部
 */
public class IgnoredNormalDependencyPluginParserInfoHandler extends AbstractPluginParserInfoHandler {
    @Override
    public boolean support(PluginParseInfo parseInfo) {
        return !parseInfo.isNormalDependencyInitializationCompleted();
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        parseInfo.setNormalDependencyInitializationCompleted(true);
        processor.next(parseInfo);
        return true;
    }
}
