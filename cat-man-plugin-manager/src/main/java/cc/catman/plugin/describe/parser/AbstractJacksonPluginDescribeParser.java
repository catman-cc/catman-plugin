package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public abstract class AbstractJacksonPluginDescribeParser implements  IPluginDescribeParser{

    private ObjectMapper objectMapper=createObjectMapper();

    protected abstract ObjectMapper createObjectMapper();

    @Override
    @SneakyThrows
    public PluginParseInfo wrapper(StandardPluginDescribe standardPluginDescribe) {
        JsonNode node = objectMapper.readTree(standardPluginDescribe.getDescribeResource().getInputStream());
        String kind=node.get("kind").asText();
        String source=node.get("source").asText();
        // 如果pluginDescribe已经包含了一些基础信息,这里的copy操作需要将原始的信息传递过来.
        // 值得注意的是,如果
        PluginParseInfo parseInfo = PluginParseInfo.builder()
                .parser(this)
                .kind(kind)
                .source(source)
                .status(EPluginParserStatus.WAIT_PARSE)
                .afterHandlers(standardPluginDescribe.getAfterHandlers())
                .afterParsers(standardPluginDescribe.getAfterParsers())
                .describeResource(standardPluginDescribe.getDescribeResource())
                .build();
        // 初次解析时,插件的描述信息只有上述内容,因为上述内容是不需要解析配置文件就可以获取的数据
        parseInfo.callAfterParsers();
        return parseInfo;
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
