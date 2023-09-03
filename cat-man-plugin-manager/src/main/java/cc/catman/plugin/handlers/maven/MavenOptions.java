package cc.catman.plugin.handlers.maven;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MavenOptions {
    /**
     * maven的可执行文件
     */
    private String mavenExecuteFile;

    private String mavenHome;

    private String localRepositoryDirectory;

    private String pluginRepositoryDirectory;

    @Builder.Default
    private IPluginStorageStrategy pluginStorageStrategy=new MavenLikePluginStorageStrategy();

    private String javaHome;

    /**
     * 私有仓库地址
     */
    private String repoUrl;

    /**
     * 用户自己的settings.xml文件地址
     */
    private String userSettingPath;

    private String globalSettingPath;

    /**
     * 是否复制插件的依赖库,到插件所处的目录下
     */
    private boolean copyLibToPluginWorkDir;

    private boolean debug;

    private String  baseDir;


}
