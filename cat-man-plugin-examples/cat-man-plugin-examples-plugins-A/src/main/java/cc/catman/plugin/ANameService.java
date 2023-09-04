package cc.catman.plugin;

import cat.man.plugin.example.api.NameService;
import cc.catman.plugin.core.annotations.ExtensionPoint;

@ExtensionPoint
public class ANameService implements NameService {
    @Override
    public String echo(String content) {
        return "A"+content;
    }
}
