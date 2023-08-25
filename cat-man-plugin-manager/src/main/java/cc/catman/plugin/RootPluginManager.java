package cc.catman.plugin;


import cc.catman.plugin.runtime.DefaultPluginManager;
import cc.catman.plugin.runtime.IPluginConfiguration;

import java.util.Collections;

public class RootPluginManager extends DefaultPluginManager {

    public static RootPluginManager from(IPluginConfiguration pluginConfiguration){
        return new RootPluginManager(pluginConfiguration);
    }
    public RootPluginManager(IPluginConfiguration pluginConfiguration) {
        super(pluginConfiguration, Collections.emptyList(), pluginConfiguration.loadProviders());
    }
}
