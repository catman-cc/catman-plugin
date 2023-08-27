package cc.catman.plugin;


import cc.catman.plugin.runtime.DefaultPluginManager;
import cc.catman.plugin.runtime.IPluginConfiguration;


public class RootPluginManager extends DefaultPluginManager {

    public static RootPluginManager from(IPluginConfiguration pluginConfiguration){
        return new RootPluginManager(pluginConfiguration);
    }
    public RootPluginManager(IPluginConfiguration pluginConfiguration) {
        super(pluginConfiguration,  pluginConfiguration.loadProviders());
    }
}
