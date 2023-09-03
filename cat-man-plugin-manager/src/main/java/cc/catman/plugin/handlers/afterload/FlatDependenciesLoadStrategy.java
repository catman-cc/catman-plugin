package cc.catman.plugin.handlers.afterload;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.runtime.IPluginManager;

import java.util.List;

public class FlatDependenciesLoadStrategy implements IDependenciesLoadStrategy{
    @Override
    public void load(IPluginManager pluginManager, List<PluginParseInfo> pluginParseInfos) {
        IPluginInstance ownerPluginInstance = pluginManager.getOwnerPluginInstance();
        IPluginManager ownerPluginManager = ownerPluginInstance.getPluginManager();
        ownerPluginManager.install(pluginParseInfos).forEach(pi->{
            pi.addReference(pluginManager.getOwnerPluginInstance());
            ownerPluginInstance.addUsed(pi);
        });
    }
}
