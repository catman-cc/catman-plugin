package cc.catman.plugin.describe.handler.raw;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.handler.AbstractPluginParserInfoHandler;
import cc.catman.plugin.runtime.IPluginConfiguration;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 因为在解析过程中,有可能获取新的插件描述文件
 * 所以这里提供一个支持解析插件描述文件的处理器,其本质只是做了一次转发操作
 */
public class RawPluginParserInfoHandler extends AbstractPluginParserInfoHandler {

    private final IPluginConfiguration pluginConfiguration;

    public RawPluginParserInfoHandler(IPluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public boolean support(PluginParseInfo parseInfo) {
        return  (!StringUtils.hasText(parseInfo.getKind()))&&
                (!StringUtils.hasText(parseInfo.getSource()))&&
                Objects.nonNull(parseInfo.getResource());
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        return pluginConfiguration.getPluginParserContext().parser(parseInfo);
    }
}
