package cc.catman.plugin.handlers.maven;

import cc.catman.plugin.core.describe.BasicPluginDescribe;

import java.io.IOException;
import java.nio.file.Path;

public interface IPluginStorageStrategy {
    Path covert(Path base, BasicPluginDescribe pluginDescribe, boolean createIfNotExist) throws IOException;
}
