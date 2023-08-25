package cc.catman.plugin.extensionPoint;

import cc.catman.plugin.extensionPoint.finder.IExtensionPointFinder;
import cc.catman.plugin.runtime.IPluginInstance;

import java.util.List;

public interface IExtensionPointManager {
    IPluginInstance getPluginInstance();

    List<ExtensionPointInfo> getExtensionPointInfos();

    List<IExtensionPointFinder> getExtensionPointFinders();

    IExtensionPointInstanceFactory getExtensionPointInstanceFactory();

    void start();
}
