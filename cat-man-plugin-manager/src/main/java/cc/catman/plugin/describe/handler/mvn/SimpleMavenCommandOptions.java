package cc.catman.plugin.describe.handler.mvn;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleMavenCommandOptions {
    /**
     * 本地仓库地址
     */
    @Builder.Default
    private String LocalRepositoryDir="~/.m2/repository";

    /**
     * 远程仓库地址,
     */
    @Builder.Default
    private String repositoryUrl="repo1.maven.org";

    /**
     *  完整的mvn命令地址
     */
    @Builder.Default
    private String mvnCommand="mvn";
}
