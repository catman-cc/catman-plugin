package cc.catman.plugin.handlers.parser;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.enums.DescribeConstants;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class AbstractJacksonPluginDescribeParserInfoHandler extends AbstractPluginParserInfoHandler {
    private ObjectMapper objectMapper=createObjectMapper();

    protected abstract ObjectMapper createObjectMapper();

    @Override
    protected List<String> withoutExclusiveParser() {
        return Collections.singletonList(DescribeConstants.NEED_PARSER_PLUGIN_DESCRIBE_FILE);
    }

    @Override
    protected boolean doSupport(PluginParseInfo parseInfo) {
       return parseInfo.getLabels().noExist(EDescribeLabel.DESCRIBE_INFO_FILLED.label())
               &&doSupportFile(parseInfo);
    }
    protected abstract boolean doSupportFile(PluginParseInfo parseInfo);

    @Override
    public List<String> lifeCycles() {
        return Collections.singletonList(ELifeCycle.PARSE.name());
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        PluginParseInfo pluginParseInfo = null;
        try {
            pluginParseInfo = objectMapper.readValue(parseInfo.getDescribeResource().getInputStream(), PluginParseInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        pluginParseInfo= processor.createNext(parseInfo,pluginParseInfo);
        try {
            pluginParseInfo.getLabels().add(EDescribeLabel.decorate("property/config-desc"),new String(StreamUtils.copyToByteArray(parseInfo.getDescribeResource().getInputStream())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pluginParseInfo.getLabels().add(EDescribeLabel.DESCRIBE_INFO_FILLED.label(),getClass().getCanonicalName());
        // 移除排他性
        pluginParseInfo.getLabels().rm(EDescribeLabel.EXCLUSIVE_PARSER.label()
                , DescribeConstants.NEED_PARSER_PLUGIN_DESCRIBE_FILE);
//        processor.next(pluginParseInfo);

        // 在下一回合,将继续在该生命周期触发执行,下一回合结束,进入下一阶段
        pluginParseInfo.setLifeCycle(parseInfo.getLifeCycle());
        processor.finish(parseInfo);
        return true;
    }
}
