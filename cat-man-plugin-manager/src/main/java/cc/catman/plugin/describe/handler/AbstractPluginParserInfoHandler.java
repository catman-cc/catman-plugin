package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginParseInfo;

import java.util.List;

public abstract class AbstractPluginParserInfoHandler implements IPluginParserInfoHandler{

    protected abstract List<String> getKinds();
    protected abstract List<String> getSources();

    public  boolean support(PluginParseInfo parseInfo){
        String source= parseInfo.getSource();
        String kind=parseInfo.getKind();
        return getKinds().stream().anyMatch(k->k.equals(kind))
                &&getSources().stream().anyMatch(s->s.equals(source));
    }

}
