package cc.catman.plugin.describe.finder;

import cc.catman.plugin.describe.PluginDescribe;

import java.util.List;

/**
 * 插件描述文件的查找器
 */
public interface IPluginDescribeFinder {

    List<PluginDescribe> finder();
}