package cc.catman.plugin;

import cat.man.plugin.example.api.NameService;
import cc.catman.plugin.core.annotations.ExtensionPoint;

@ExtensionPoint
public class BNameService implements NameService {
    @Override
    public String echo(String content) {
        return "B"+content;
    }
}
