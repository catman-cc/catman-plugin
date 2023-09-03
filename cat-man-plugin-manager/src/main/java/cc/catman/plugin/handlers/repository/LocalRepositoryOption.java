package cc.catman.plugin.handlers.repository;

import cc.catman.plugin.handlers.maven.IPluginStorageStrategy;
import cc.catman.plugin.handlers.maven.MavenLikePluginStorageStrategy;
import lombok.Data;

import java.nio.file.Path;

@Data
public class LocalRepositoryOption {
    /**
     * 本地仓库地址
     */
    private Path repositoryDir;

    /**
     * 插件存储时策略
     */
    private IPluginStorageStrategy pluginStorageStrategy=new MavenLikePluginStorageStrategy();
}
