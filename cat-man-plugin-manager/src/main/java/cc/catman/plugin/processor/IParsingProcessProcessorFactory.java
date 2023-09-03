package cc.catman.plugin.processor;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.runtime.IPluginManager;

import java.util.List;
import java.util.function.Predicate;

public interface IParsingProcessProcessorFactory {
    IParsingProcessProcessor create(List<PluginParseInfo> parseInfos, IPluginManager ownerPluginManager);

    List<IPluginParserInfoHandler> getHandlers();
}
