package cc.catman.plugin.handlers;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.processor.IParsingProcessProcessor;

import java.util.Collections;
import java.util.List;

public interface IPluginParserInfoHandler {
    default List<String> lifeCycles(){
        return Collections.emptyList();
    }
    boolean support(PluginParseInfo parseInfo);
    default boolean handler(IParsingProcessProcessor processor,PluginParseInfo parseInfo){
        return true;
    }
}
