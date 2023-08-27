package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginParseInfo;

import java.util.Collections;
import java.util.List;

public abstract class AbstractPluginParserInfoHandler implements IPluginParserInfoHandler {
    protected List<String> getKinds() {
        return Collections.emptyList();
    }

    protected List<String> getSources() {
        return Collections.emptyList();
    }

    public boolean support(PluginParseInfo parseInfo) {
        String source = parseInfo.getSource();
        String kind = parseInfo.getKind();
        // 这里很重要,用于处理第三方依赖jar包的关键
        return parseInfo.isNormalDependencyInitializationCompleted()
               && getKinds().stream().anyMatch(k -> k.equals(kind))
               && getSources().stream().anyMatch(s -> s.equals(source));
    }
    @SuppressWarnings("unchecked")
    public <T extends PluginParseInfo> T covert(PluginParseInfo parseInfo, Class<T> clazz) {
        T t = clazz.isAssignableFrom(parseInfo.getClass())
                ? (T) parseInfo
                : parseInfo.decode(clazz);
        t.getHandlerChain().add(this);
        return t;
    }
}
