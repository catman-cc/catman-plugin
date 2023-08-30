//package cc.catman.plugin.describe.handler.maven;
//
//import cc.catman.plugin.describe.PluginParseInfo;
//import cc.catman.plugin.describe.StandardPluginDescribe;
//import cc.catman.plugin.describe.parser.IPluginDescribeParser;
//import junit.framework.TestCase;
//
//public class MavenDownloadPluginParserInfoHandlerTest extends TestCase {
//    public static MavenOptions mavenOptions=MavenOptions.builder()
//            .mavenExecuteFile("mvn")
//            .globalSettingPath("/Users/jpanda/.m2/settings.xml")
//            .userSettingPath("/Users/jpanda/work/codes/customer/cat-man/cat-man-plugin/cat-man-plugin-manager/src/test/resources/maven-user-settings.xml")
//            .repoUrl("http://127.0.0.1:31081/repository/maven-releases/")
//            .localRepositoryDirectory("/Users/jpanda/.m2")
//            .pluginRepositoryDirectory("/Users/jpanda/work/temp")
//            .mavenHome("/Users/jpanda/.sdkman/candidates/maven/current/")
//            .baseDir("/Users/jpanda/work/codes/customer/cat-man/cat-man-plugin/cat-man-plugin-manager/tmp")
//            .debug(true)
//            .build();
//    public static PluginParseInfo parseInfo=initPluginParseInfo();
//
//    private static PluginParseInfo initPluginParseInfo() {
//        MavenPluginParseInfo p = MavenPluginParseInfo.builder()
//                .group("cc.catman.plugin")
//                .name("cat-man-plugin-examples-plugins")
//                .version("1.0.0")
//                .build();
//        p.setParser(new IPluginDescribeParser() {
//            @Override
//            public boolean supports(StandardPluginDescribe standardPluginDescribe) {
//                return true;
//            }
//
//            @Override
//            public PluginParseInfo wrapper(StandardPluginDescribe standardPluginDescribe) {
//                return PluginParseInfo.builder().build();
//            }
//
//            @Override
//            public <T extends StandardPluginDescribe> T decode(PluginParseInfo parseInfo, Class<T> clazz) {
//                return (T) p;
//            }
//        });
//        return p;
//    }
//
//    public void xxxtestDownloadPlugin(){
//        MavenDownloadPluginParserInfoHandler handler=new MavenDownloadPluginParserInfoHandler(mavenOptions);
//        handler.handler(parseInfo);
//
//    }
//}