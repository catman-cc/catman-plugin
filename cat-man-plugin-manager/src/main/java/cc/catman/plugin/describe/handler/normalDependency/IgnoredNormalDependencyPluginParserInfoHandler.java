package cc.catman.plugin.describe.handler.normalDependency;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.handler.IPluginParserInfoHandler;

import java.util.Collections;
import java.util.List;

/**
 * 一个直接忽略普通第三方依赖的处理器,如果你不需要插件系统管理你的第三方依赖,那么请添加该处理器,到处理链的顶部
 */
public class IgnoredNormalDependencyPluginParserInfoHandler implements IPluginParserInfoHandler {
    @Override
    public boolean support(PluginParseInfo parseInfo) {
        return !parseInfo.isNormalDependencyInitializationCompleted();
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        parseInfo.setNormalDependencyInitializationCompleted(true);
        return Collections.singletonList(parseInfo);
    }
}
