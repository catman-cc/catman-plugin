package cc.catman.plugin.handlers.parser;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.describe.StandardPluginDescribe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.util.Optional;

public class JSONJacksonPluginDescribeParserInfoHandler extends AbstractJacksonPluginDescribeParserInfoHandler{
    @Override
    protected ObjectMapper createObjectMapper() {
        return new JsonMapper();
    }


    @Override
    protected boolean doSupportFile(PluginParseInfo standardPluginDescribe) {
        return Optional.ofNullable(standardPluginDescribe.getDescribeResource())
                .map(resource -> Optional.ofNullable(resource.getFilename())
                        .map(filename-> filename.endsWith(".json"))
                        .orElse(false)
                ).orElse(false);
    }
}
