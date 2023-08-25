package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.PluginDescribe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JsonJacksonPluginDescribeParser extends AbstractJacksonPluginDescribeParser{
    @Override
    protected ObjectMapper createObjectMapper() {
        return new JsonMapper();
    }

    @Override
    public boolean supports(PluginDescribe pluginDescribe) {
        String filename = pluginDescribe.getResource().getFilename();
        if (filename != null) {
            return filename.endsWith(".json");
        }
        return false;
    }
}
