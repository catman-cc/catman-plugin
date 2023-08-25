package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.ArrayList;

public abstract class AbstractJacksonPluginDescribeParser implements  IPluginDescribeParser{

    private ObjectMapper objectMapper=createObjectMapper();

    protected abstract ObjectMapper createObjectMapper();

    @Override
    @SneakyThrows
    public PluginParseInfo wrapper(PluginDescribe pluginDescribe) {
        JsonNode node = objectMapper.readTree(pluginDescribe.getResource().getInputStream());
        String kind=node.get("kind").asText();
        String source=node.get("source").asText();
        return PluginParseInfo.builder().parser(this).pluginDescribe(pluginDescribe).kind(kind).source(source).build();
    }

    @Override
    @SneakyThrows
    public <T extends PluginDescribe> T decode(PluginParseInfo parseInfo,Class<T> clazz) {
        T res=this.objectMapper.readValue(parseInfo.getPluginDescribe().getResource().getInputStream(),clazz);
        res.copyFrom(parseInfo.getPluginDescribe());
        return res;
    }
}
