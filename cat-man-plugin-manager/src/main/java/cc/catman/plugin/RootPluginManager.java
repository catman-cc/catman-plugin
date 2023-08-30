package cc.catman.plugin;


import cc.catman.plugin.options.PluginOptions;
import cc.catman.plugin.runtime.DefaultPluginManager;
import cc.catman.plugin.runtime.IPluginConfiguration;


public class RootPluginManager extends DefaultPluginManager {

    public static RootPluginManager from(IPluginConfiguration pluginConfiguration){
        return new RootPluginManager(pluginConfiguration, PluginOptions.of());
    }

    public static RootPluginManager from(IPluginConfiguration pluginConfiguration, PluginOptions option){
        return new RootPluginManager(pluginConfiguration,option);
    }
    public RootPluginManager(IPluginConfiguration pluginConfiguration, PluginOptions option) {
        super(pluginConfiguration,  pluginConfiguration.loadProviders(),option);
    }
}
