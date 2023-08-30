package cc.catman.plugin.describe.parser;

import cc.catman.plugin.describe.StandardPluginDescribe;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.util.Optional;

public class JsonJacksonPluginDescribeParser extends AbstractJacksonPluginDescribeParser{
    @Override
    protected ObjectMapper createObjectMapper() {
        return new JsonMapper();
    }

    
    @Override
    protected boolean doSupports(StandardPluginDescribe standardPluginDescribe) {
        return Optional.ofNullable(standardPluginDescribe.getDescribeResource())
                .map(resource -> Optional.ofNullable(resource.getFilename())
                        .map(filename-> filename.endsWith(".json"))
                        .orElse(false)
                ).orElse(false);
    }
}
