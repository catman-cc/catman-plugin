package cc.catman.plugin.extensionPoint;

import cc.catman.plugin.extensionPoint.finder.DefaultExtensionPointFinder;
import cc.catman.plugin.runtime.IPluginInstance;

public class DefaultExtensionPointManagerFactory implements IExtensionPointManagerFactory{
    @Override
    public IExtensionPointManager create(IPluginInstance pluginInstance) {
        DefaultExtensionPointManager extensionPointManager=new DefaultExtensionPointManager(pluginInstance);
        post(extensionPointManager);
        return extensionPointManager;
    }

    public void post( DefaultExtensionPointManager extensionPointManager){
        extensionPointManager.extensionPointFinders.add(new DefaultExtensionPointFinder(extensionPointManager.pluginInstance));
//        extensionPointManager.extensionPointMatchers.add(new DefaultExtensionPointMatcher());
    }
}
