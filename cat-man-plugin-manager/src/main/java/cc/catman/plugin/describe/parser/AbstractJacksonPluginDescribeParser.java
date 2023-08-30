package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.DescribeConstants;
import cc.catman.plugin.describe.enmu.EDescribeLabel;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.Optional;

public abstract class AbstractJacksonPluginDescribeParser implements  IPluginDescribeParser{

    private ObjectMapper objectMapper=createObjectMapper();

    protected abstract ObjectMapper createObjectMapper();

    @Override
    public boolean supports(StandardPluginDescribe standardPluginDescribe) {
        // 排他性验证
       return standardPluginDescribe.getLabels()
                .notExistLabelOrLabelHasValue(
                        EDescribeLabel.EXCLUSIVE_PARSER.label()
                        , DescribeConstants.NEED_PARSER_PLUGIN_DESCRIBE_FILE
                )&& doSupports(standardPluginDescribe);
    }

    protected abstract boolean doSupports(StandardPluginDescribe standardPluginDescribe);

    @Override
    @SneakyThrows
    public PluginParseInfo wrapper(StandardPluginDescribe standardPluginDescribe) {
        PluginParseInfo pluginParseInfo = objectMapper.readValue(standardPluginDescribe.getDescribeResource().getInputStream(), PluginParseInfo.class);
        pluginParseInfo.setParser(this);
        pluginParseInfo.setStatus(EPluginParserStatus.WAIT_PARSE);
        pluginParseInfo.setAfterHandlers(standardPluginDescribe.getAfterHandlers());
        pluginParseInfo.setAfterParsers(standardPluginDescribe.getAfterParsers());
        pluginParseInfo.setDescribeResource(standardPluginDescribe.getDescribeResource());
        pluginParseInfo.callAfterParsers();
        pluginParseInfo.getLabels().add(EDescribeLabel.DESCRIBE_FILE_PARSED.label(),getClass().getCanonicalName());
        // 移除排他性
        pluginParseInfo.getLabels().rm(EDescribeLabel.EXCLUSIVE_PARSER.label()
                , DescribeConstants.NEED_PARSER_PLUGIN_DESCRIBE_FILE);
        return pluginParseInfo;
    }

    @Override
    @SneakyThrows
    public <T extends StandardPluginDescribe> T decode(PluginParseInfo parseInfo, Class<T> clazz) {
        T res=this.objectMapper.readValue(parseInfo.getDescribeResource().getInputStream(),clazz);
        // 后执行,为了让运行时修改的数据,覆盖默认信息
        res.copyFrom(parseInfo);
        return res;
    }
}
