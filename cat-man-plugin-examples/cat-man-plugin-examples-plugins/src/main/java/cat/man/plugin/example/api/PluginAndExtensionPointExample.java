package cat.man.plugin.example.api;

import cc.catman.plugin.core.IPlugin;
import cc.catman.plugin.core.annotations.ExtensionPoint;
import cc.catman.plugin.core.annotations.Gav;
import cc.catman.plugin.core.annotations.Plugin;

@ExtensionPoint
@Plugin(name = "cat-man-plugin-example", group = "cc.catman.plugin", version = "1.0.4", dependencies = {
        @Gav(name = "cat-man-plugin-examples-plugins-A")
})
public class PluginAndExtensionPointExample implements NameService, IPlugin {
    @Override
    public String echo(String content) {
        return getClass().getName() + ":" + content;
    }

    @Override
    public void onload() {

    }

    @Override
    public void beforeUnload() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

    }
}