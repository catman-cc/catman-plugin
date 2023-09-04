package cc.catman.plugin.handlers.finished;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import cc.catman.plugin.runtime.EPluginStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ReadPluginParserInfoHandler extends AbstractPluginParserInfoHandler {
    @Override
    public List<String> lifeCycles() {
        return Collections.singletonList(ELifeCycle.FINISHED.name());
    }

    @Override
    public boolean support(PluginParseInfo parseInfo) {
        return Optional.ofNullable(parseInfo.getPluginInstance()).isPresent();
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        parseInfo.getPluginInstance().updateStatus(EPluginStatus.READY);
        return true;
    }
}
