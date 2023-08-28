package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.StandardPluginDescribe;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonJacksonPluginDescribeParser extends AbstractJacksonPluginDescribeParser{
    @Override
    protected ObjectMapper createObjectMapper() {
        return new JsonMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public boolean supports(StandardPluginDescribe standardPluginDescribe) {
        String filename = standardPluginDescribe.getResource().getFilename();
        if (filename != null) {
            return filename.endsWith(".json");
        }
        return false;
    }
}
