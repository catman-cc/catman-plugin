package cc.catman.plugin.handlers;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.label.ILabelAbility;
import cc.catman.plugin.core.label.L;
import cc.catman.plugin.core.label.filter.ILabelFilter;
import cc.catman.plugin.enums.DescribeConstants;
import cc.catman.plugin.processor.IParsingProcessProcessor;

import java.util.Collections;
import java.util.List;

@L(name = DescribeConstants.HANDLER_FEATURE_NAME, value = "handler")
public interface IPluginParserInfoHandler extends ILabelAbility , ILabelFilter {
    default List<String> lifeCycles(){
        return Collections.emptyList();
    }
    boolean support(PluginParseInfo parseInfo);
    default boolean handler(IParsingProcessProcessor processor,PluginParseInfo parseInfo){
        return true;
    }
}
