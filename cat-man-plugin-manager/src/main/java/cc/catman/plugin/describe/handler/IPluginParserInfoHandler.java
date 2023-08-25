package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginParseInfo;

import java.util.List;

public interface IPluginParserInfoHandler {
    boolean support(PluginParseInfo parseInfo);
    List<PluginParseInfo> handler(PluginParseInfo parseInfo);
}
