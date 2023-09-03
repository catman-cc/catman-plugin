package cc.catman.plugin.processor;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.handlers.PluginParseInfoHelper;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.runtime.IPluginManager;

import java.util.List;
import java.util.Map;

/**
 * 解析流程处理器
 */
public interface IParsingProcessProcessor {
    List<IPluginInstance> process();



    void finish(PluginParseInfo parseInfo);

    void next(PluginParseInfo parseInfo);

    PluginParseInfo createNext(PluginParseInfo from);

    PluginParseInfo createNext(PluginParseInfo from,PluginParseInfo parseInfo);



    boolean beforeLifeCycles(String lifeCycle,String before);

    boolean afterLifeCycles(String lifeCycle,String after);

    IParsingProcessProcessor add(IPluginParserInfoHandler parserInfoHandler);

    PluginParseInfoHelper getPluginParseInfoHelper();


    IPluginManager getOwnerPluginManager();

    default IPluginInstance registryPluginInstance(PluginParseInfo parseInfo){
        return getOwnerPluginManager().registryPluginInstance(parseInfo);
    }

    Map<Integer, List<IPluginInstance>> getRoundPluginInstances();
}
