package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;

import java.util.List;

public interface IPluginParserContext   {

    List<PluginParseInfo> parser(PluginDescribe pluginDescribe);

}
