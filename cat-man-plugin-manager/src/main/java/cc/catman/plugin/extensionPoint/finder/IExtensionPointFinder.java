package cc.catman.plugin.extensionPoint.finder;

import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;

import java.util.stream.Stream;

/**
 * 扩展点查找器
 */
public interface IExtensionPointFinder {

    Stream<ExtensionPointInfo> find(PluginDescribe pluginDescribe);
}
