package cc.catman.plugin.extensionPoint;

import cc.catman.plugin.runtime.IPluginInstance;

public interface IExtensionPointManagerFactory {

    IExtensionPointManager create(IPluginInstance pluginInstance);
}
