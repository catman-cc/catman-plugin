package cc.catman.plugin.handlers;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.EDescribeLabel;

import java.util.Collections;
import java.util.List;

public abstract class AbstractPluginParserInfoHandler implements IPluginParserInfoHandler {
    public boolean support(PluginParseInfo parseInfo) {
        return parseInfo.getLabels().notExistLabelOrLabelHasAnyValue(EDescribeLabel.EXCLUSIVE_PARSER.derive(parseInfo.getLifeCycle()),withoutExclusiveParser())
               &&doSupport(parseInfo);
    }

    protected abstract boolean doSupport(PluginParseInfo parseInfo);

    protected List<String> withoutExclusiveParser(){
        return Collections.emptyList();
    }

}
