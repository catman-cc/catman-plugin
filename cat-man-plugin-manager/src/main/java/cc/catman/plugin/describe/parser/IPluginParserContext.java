package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;

import java.util.List;

public interface IPluginParserContext   {

    List<PluginParseInfo> parser(StandardPluginDescribe standardPluginDescribe);
    IPluginParserContext add(IPluginDescribeParser parser);
}
