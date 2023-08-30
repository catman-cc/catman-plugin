package cc.catman.plugin.describe.handler.mvn;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.handler.maven.MavenOptions;
import cc.catman.plugin.describe.handler.maven.NormalMavenLibsDownloadPluginParserInfoHandler;
import cc.catman.plugin.describe.parser.IPluginDescribeParser;
import cc.catman.plugin.describe.resources.JarResourceBrowser;
import junit.framework.TestCase;
import lombok.SneakyThrows;
import org.springframework.core.io.UrlResource;

public class NormalMavenLibsDownloadPluginParserInfoHandlerTest extends TestCase {
    public static MavenOptions mavenOptions=MavenOptions.builder()
            .mavenExecuteFile("mvn")
            .globalSettingPath("/Users/jpanda/.m2/settings.xml")
            .userSettingPath("/Users/jpanda/work/codes/customer/cat-man/cat-man-plugin/cat-man-plugin-manager/src/test/resources/maven-user-settings.xml")
            .repoUrl("http://127.0.0.1:31081/repository/maven-snapshots/")
            .localRepositoryDirectory("/Users/jpanda/.m2/repository")
            .pluginRepositoryDirectory("/Users/jpanda/work/temp")
            .mavenHome("/Users/jpanda/.sdkman/candidates/maven/current/")
            .baseDir("/Users/jpanda/work/temp/cc/catman/plugin/cat-man-plugin-examples-plugins/1.0.0")
            .debug(true)
            .build();
    public static PluginParseInfo parseInfo=initPluginParseInfo();

    @SneakyThrows
    private static PluginParseInfo initPluginParseInfo() {
        PluginParseInfo p = PluginParseInfo.builder()
                .group("cc.catman.plugin")
                .name("cat-man-plugin-examples-plugins")
                .version("1.0.0")
                .baseDir("/Users/jpanda/work/temp/cc/catman/plugin/cat-man-plugin-examples-plugins/1.0.0")
                .normalDependencyLibrariesDescrbieResource(new UrlResource(JarResourceBrowser.wrapperToJarURL("/Users/jpanda/work/temp/cc/catman/plugin/cat-man-plugin-examples-plugins/1.0.0/cat-man-plugin-examples-plugins-1.0.0-SNAPSHOT.jar",
                        "META-INF/cat-man-plugin/cat-man-plugin-maven.deps")))
                .build();
        p.setParser(new IPluginDescribeParser() {
            @Override
            public boolean supports(StandardPluginDescribe standardPluginDescribe) {
                return true;
            }

            @Override
            public PluginParseInfo wrapper(StandardPluginDescribe standardPluginDescribe) {
                return PluginParseInfo.builder().build();
            }

            @Override
            public <T extends StandardPluginDescribe> T decode(PluginParseInfo parseInfo, Class<T> clazz) {
                return (T) p;
            }
        });
        return p;
    }
        public void testHandler() {
            NormalMavenLibsDownloadPluginParserInfoHandler handler=new NormalMavenLibsDownloadPluginParserInfoHandler(mavenOptions);
            handler.handler(parseInfo);
    }
}