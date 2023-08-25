package cc.catman.plugin.extensionPoint.finder.scan;

import cc.catman.plugin.extensionPoint.ExtensionPointInfo;

public interface IExtensionPointProcessor {
    void handle(ExtensionPointInfo extensionPointInfo);
}
