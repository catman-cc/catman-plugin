package cc.catman.plugin.describe.handler.maven;

import cc.catman.plugin.describe.BasicPluginDescribe;
import cc.catman.plugin.describe.PluginDescribe;

import java.io.IOException;
import java.nio.file.Path;

public interface IPluginStorageStrategy {
    Path covert(Path base, BasicPluginDescribe pluginDescribe, boolean createIfNotExist) throws IOException;
}
