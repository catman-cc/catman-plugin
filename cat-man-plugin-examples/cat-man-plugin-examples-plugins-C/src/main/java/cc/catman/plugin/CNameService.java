package cc.catman.plugin;

import cat.man.plugin.example.api.NameService;

public class CNameService implements NameService {
    @Override
    public String echo(String content) {
        return "C"+content;
    }
}
