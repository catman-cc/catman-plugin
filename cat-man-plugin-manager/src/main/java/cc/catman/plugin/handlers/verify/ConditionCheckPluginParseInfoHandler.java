package cc.catman.plugin.handlers.verify;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;

import java.util.Collections;
import java.util.List;

public class ConditionCheckPluginParseInfoHandler extends AbstractPluginParserInfoHandler {
    @Override
    public List<String> lifeCycles() {
        return Collections.singletonList(ELifeCycle.VERIFY.name());
    }

    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
       return parseInfo.getLabels().hasPrefix(EConditionLabel.TEST.v());
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {

        return super.handler(processor, parseInfo);
    }
}
