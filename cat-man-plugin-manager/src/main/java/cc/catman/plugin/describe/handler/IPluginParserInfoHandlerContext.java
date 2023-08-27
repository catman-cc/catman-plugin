package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginParseInfo;

import java.util.List;
import java.util.function.Predicate;

public interface IPluginParserInfoHandlerContext {
    List<PluginParseInfo> handler(PluginParseInfo parseInfo);
    List<IPluginParserInfoHandler> getHandlers();
    IPluginParserInfoHandlerContext addHandler(IPluginParserInfoHandler handler);
    IPluginParserInfoHandlerContext addFirst(IPluginParserInfoHandler handler);

    IPluginParserInfoHandlerContext remove(Predicate<IPluginParserInfoHandler> test);

    boolean replaceFirst(Predicate<IPluginParserInfoHandler> test, IPluginParserInfoHandler handler);
}
