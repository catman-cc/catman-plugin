package cc.catman.plugin.handlers.repository;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class LocalRepositoryPluginParserInfoHandler extends AbstractPluginParserInfoHandler {
    protected  LocalRepositoryOption option;

    public LocalRepositoryPluginParserInfoHandler(LocalRepositoryOption option) {
        this.option = option;
    }

    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
        // 如果一个插件没有描述文件,没有工作目录,那很明显,插件需要由插件市场来处理,那就尝试从maven仓库下载
        return parseInfo.getLabels().noExist(EDescribeLabel.EXCLUSIVE_PARSER.label())
               && !StringUtils.hasText(parseInfo.getBaseDir())
               &&parseInfo.getDescribeResource()==null
               &&parseInfo.hasGAV();
    }

    @Override
    public List<String> lifeCycles() {
        return Arrays.asList(ELifeCycle.ACHIEVE.name(),ELifeCycle.UNKNOWN.name());
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        try {
            // 获取仓库地址
            Path pluginDir = option.getPluginStorageStrategy().covert(option.getRepositoryDir(), parseInfo, true);
            // 然后尝试从该目录下加载插件
            parseInfo.setBaseDir(pluginDir.toString());
            processor.next(parseInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


}
