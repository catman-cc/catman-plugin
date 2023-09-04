package cc.catman.plugin;

import cc.catman.plugin.core.annotations.Gav;
import cc.catman.plugin.core.annotations.Plugin;

@Plugin(name = "cat-man-plugin-examples-plugins-A", group = "cc.catman.plugin", version = "1.0.5", dependencies = {
        @Gav(name = "cat-man-plugin-examples-plugins-B")
})
public class APlugin {
}
