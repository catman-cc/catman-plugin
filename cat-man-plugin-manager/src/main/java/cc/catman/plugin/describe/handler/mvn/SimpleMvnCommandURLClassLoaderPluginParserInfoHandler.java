package cc.catman.plugin.describe.handler.mvn;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import cc.catman.plugin.describe.handler.AbstractURLClassLoaderPluginParserInfoHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
// TODO 获取考虑使用maven-invoke
public class SimpleMvnCommandURLClassLoaderPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {

    public static final String SUPPORT_SOURCE = EPluginSource.LOCAL.name();
    public static final String SUPPORT_KIND = EPluginKind.MAVEN.name();

    protected SimpleMavenCommandOptions options;

    public SimpleMvnCommandURLClassLoaderPluginParserInfoHandler(SimpleMavenCommandOptions options) {
        this.options = options;
//        if ("mvn".equals(options.getMvnCommand().trim())){
//            Optional.ofNullable(System.getenv().get("MAVEN_HOME"))
//                    .ifPresent(dir->{
//                        this.options.setMvnCommand(Paths.get(dir,"bin","mvn").toString());
//                    });
//        }
    }
    @Override
    public boolean support(PluginParseInfo parseInfo) {
        String source = parseInfo.getSource();
        String kind = parseInfo.getKind();
        // 这里很重要,用于处理第三方依赖jar包的关键
        return !parseInfo.isNormalDependencyInitializationCompleted()
               && getKinds().stream().anyMatch(k -> k.equals(kind))
               && getSources().stream().anyMatch(s -> s.equals(source));
    }
    @Override
    protected List<String> getKinds() {
        return Collections.singletonList(SUPPORT_KIND);
    }

    @Override
    protected List<String> getSources() {
        return Collections.singletonList(SUPPORT_SOURCE);
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        MvnCommandPluginParseInfo mppd=covert(parseInfo,MvnCommandPluginParseInfo.class);
        try {
            String command = String.format("%s dependency:get -DgroupId=%s -DartifactId=%s -Dversion=%s -Dclassifier=sources -DrepoUrl=%s"
                    ,options.getMvnCommand()
                    ,mppd.group
                    ,mppd.name
                    ,mppd.version
                    ,options.getRepositoryUrl()
            ); // 替换为你要执行的命令
            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command(command.split(" "));
//                    .command("pwd");
//                    .command(command);
//                    .command(command.split(" "))
//                    .directory(Paths.get(options.getLocalRepositoryDir()).toFile());
            Process process = processBuilder.start();

            // 获取命令执行的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("命令执行结束，退出码：" + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
