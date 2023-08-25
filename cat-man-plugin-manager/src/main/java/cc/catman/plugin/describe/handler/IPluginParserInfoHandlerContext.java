package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginParseInfo;

import java.util.List;

public interface IPluginParserInfoHandlerContext {
    List<PluginParseInfo> handler(PluginParseInfo parseInfo);
}
