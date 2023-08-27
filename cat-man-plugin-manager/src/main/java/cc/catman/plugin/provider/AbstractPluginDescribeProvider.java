package cc.catman.plugin.provider;

import cc.catman.plugin.describe.parser.IPluginParserContext;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractPluginDescribeProvider implements IPluginDescribeProvider{
    @Getter
    @Setter
    protected IPluginParserContext pluginParserContext;
}
