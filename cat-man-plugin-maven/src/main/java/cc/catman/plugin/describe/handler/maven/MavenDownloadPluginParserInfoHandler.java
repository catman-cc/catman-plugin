package cc.catman.plugin.describe.handler.maven;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.handler.IPluginParserInfoHandler;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;

import java.util.List;

/**
 * TODO 感觉不太好,再想想...
 */
public class MavenDownloadPluginParserInfoHandler implements IPluginParserInfoHandler {
    @Override
    public boolean support(PluginParseInfo parseInfo) {
        return false;
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        InvocationRequest request = new DefaultInvocationRequest();

        return null;
    }
}
