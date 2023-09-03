package cc.catman.plugin.handlers.maven;

import cc.catman.plugin.core.describe.BasicPluginDescribe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MavenLikePluginStorageStrategy implements IPluginStorageStrategy {
    @Override
    public Path covert(Path base, BasicPluginDescribe pluginDescribe, boolean createIfNotExist) throws IOException {
        String s = (pluginDescribe.getGroup() + File.separator + pluginDescribe.getName() + File.separator).replaceAll("\\.", File.separator);
        Path dir = base.resolve(Paths.get(s,pluginDescribe.getVersion())).normalize();
        return Files.createDirectories(dir).toAbsolutePath();
    }
}
