package cat.man.plugin.example.api;

import cc.catman.plugin.core.annotations.ExtensionPoint;

@ExtensionPoint
public class PandaNameService implements NameService {
    @Override
    public String echo(String content) {
        return "panda:"+content;
    }
}
