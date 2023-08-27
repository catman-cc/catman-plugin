package cc.catman.plugin.describe.handler.mvn;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import cc.catman.plugin.describe.handler.AbstractURLClassLoaderPluginParserInfoHandler;

import java.util.Collections;
import java.util.List;

public class SimpleMvnCommandURLClassLoaderPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {

    public static final String SUPPORT_SOURCE = EPluginSource.LOCAL.name();
    public static final String SUPPORT_KIND = EPluginKind.JAR.name();

    @Override
    protected List<String> getKinds() {
        return Collections.singletonList(SUPPORT_KIND);
    }

    @Override
    protected List<String> getSources() {
        return Collections.singletonList(SUPPORT_SOURCE);
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        return null;
    }
}
