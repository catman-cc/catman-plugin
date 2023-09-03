package cc.catman.plugin.extensionPoint.finder;

import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;

import java.util.stream.Stream;

/**
 * 扩展点查找器
 */
public interface IExtensionPointFinder {

    Stream<ExtensionPointInfo> find(StandardPluginDescribe standardPluginDescribe);
}
