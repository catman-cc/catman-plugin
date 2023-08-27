package cat.man.plugin.example.api;

import cc.catman.plugin.core.annotations.ExtensionPoint;

@ExtensionPoint
public class CatNameService implements NameService{
    @Override
    public String echo(String content) {
        return "cat"+content;
    }
}
