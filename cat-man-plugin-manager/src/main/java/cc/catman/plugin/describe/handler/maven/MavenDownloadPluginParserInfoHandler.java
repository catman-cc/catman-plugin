package cc.catman.plugin.describe.handler.maven;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import cc.catman.plugin.describe.handler.AbstractURLClassLoaderPluginParserInfoHandler;
import lombok.SneakyThrows;
import org.apache.maven.shared.invoker.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * TODO 感觉不太好,再想想...
 * 对这个方案还是不太喜欢...
 */
public class MavenDownloadPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {
    private MavenOptions mavenOptions;
    private Invoker invoker ;

    public MavenDownloadPluginParserInfoHandler(MavenOptions mavenOptions) {
        this.mavenOptions = mavenOptions;
        this.invoker=crateInvoke(mavenOptions);
    }

    protected Invoker crateInvoke(MavenOptions options){
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenExecutable(Paths.get( Optional.ofNullable(options.getMavenExecuteFile()).orElseThrow(()-> new RuntimeException(""))).toFile());

        Optional.ofNullable( options.getMavenHome())
                        .ifPresent(mh->{
                            invoker.setMavenHome(Paths.get(mh).toFile());
                        });

        Optional.ofNullable( options.getLocalRepositoryDirectory())
                .ifPresent(lr->{
                    invoker.setLocalRepositoryDirectory(Paths.get(lr).toFile());
                });

        return invoker;
    }

    public InvocationRequest createInvocationRequestCopy(Invoker invoker,MavenPluginParseInfo mavenPluginParseInfo){
        InvocationRequest request = new DefaultInvocationRequest();

        Optional.ofNullable(this.mavenOptions.getUserSettingPath()).ifPresent(settings->{
            request.setUserSettingsFile(Paths.get(settings).toFile());
        });
        Optional.ofNullable(this.mavenOptions.getGlobalSettingPath()).ifPresent(settings->{
            request.setGlobalSettingsFile(Paths.get(settings).toFile());
        });
        Optional.ofNullable( this.mavenOptions.getJavaHome()).ifPresent(jh->{
            request.setJavaHome(Paths.get(jh).toFile());
        });

        Optional.ofNullable(this.mavenOptions.getBaseDir()).ifPresent(bd->{
            request.setBaseDirectory(Paths.get(bd).toFile());
        });
        request.setDebug(this.mavenOptions.isDebug());
        // dependency:copy -Dartifact=#maven.com.google.guava:lombok:1.16.10 -DoutputDirectory=./
        List<String> list = new ArrayList<>(Arrays.asList(ECommand.DEPENDENCY_COPY.getCommand(),
                "--debug",
                "-Dartifact=" + mavenPluginParseInfo.getGroup()+":"+mavenPluginParseInfo.getName()+":"+mavenPluginParseInfo.getVersion(),
                "-DoutputDirectory=" + mavenPluginParseInfo.getPluginDir()));

        Optional.ofNullable(this.mavenOptions.getRepoUrl())
                .ifPresent(ru->{
                    list.add("-DrepoUrl=" + ru);
                });

        request.setGoals(list);

        return request;
    }

    public InvocationRequest createInvocationRequest(Invoker invoker,MavenPluginParseInfo mavenPluginParseInfo){
        InvocationRequest request = new DefaultInvocationRequest();
        List<String> list = Arrays.asList("dependency:get",
                "--debug",
                "-DgroupId=" + mavenPluginParseInfo.getGroup(),
                "-DartifactId=" + mavenPluginParseInfo.getName(),
                "-Dversion=" + mavenPluginParseInfo.getVersion());

        Optional.ofNullable(this.mavenOptions.getRepoUrl())
                .ifPresent(ru->{
                    list.add("-DrepoUrl=" + ru);
                });

        Optional.ofNullable(this.mavenOptions.getUserSettingPath()).ifPresent(settings->{
            request.setUserSettingsFile(Paths.get(settings).toFile());
        });

        Optional.ofNullable( this.mavenOptions.getJavaHome()).ifPresent(jh->{
            request.setJavaHome(Paths.get(jh).toFile());
        });

        Optional.ofNullable(this.mavenOptions.getBaseDir()).ifPresent(bd->{
            request.setBaseDirectory(Paths.get(bd).toFile());
        });
        request.setDebug(this.mavenOptions.isDebug());

        request.setGoals(list);

        return request;
    }

    protected InvocationRequest beforeInvocationRequestExec(InvocationRequest request,Invoker invoker,MavenPluginParseInfo mavenPluginParseInfo){
        return request;
    }

    @Override
    public boolean support(PluginParseInfo parseInfo) {
        return false;
    }


    @SneakyThrows
    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        MavenPluginParseInfo mppi=covert(parseInfo, MavenPluginParseInfo.class);

        Path pluginDir = this.mavenOptions.getPluginStorageStrategy().covert(Paths.get(this.mavenOptions.getPluginRepositoryDirectory()), parseInfo, true);

        mppi.setPluginDir(pluginDir);

        // 获取插件的gav信息,然后下载该插件到指定的目录下
        InvocationRequest request = createInvocationRequestCopy(this.invoker, mppi);
        request=beforeInvocationRequestExec(request,this.invoker,mppi);

        InvocationResult result = this.invoker.execute(request);
        if (result.getExitCode()!=0){
            // TODO 异常
        }
        // 转换成其他插件,默认是jar,但是这里无法继续转换为PluginParseInfo了,同时没有对应的插件描述信息
        // 所以他需要一个处理器来帮助他获取jar对应的描述信息.
        PluginParseInfo reParserInfo=PluginParseInfo.builder()
                .baseDir(pluginDir.toString())
                .status(EPluginParserStatus.RE_PARSE)
                .build();
        return Collections.singletonList(reParserInfo);
    }

    public void invoke() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        String[] strings = {
                "dependency:get",
                "--debug",
                "-DrepoUrl=https://repo1.maven.org/maven2",
                "-DgroupId=org.apache.maven.plugin-tools",
                "-DartifactId=maven-plugin-annotations",
                "-Dversion=3.9.0",
        };
        request.setGoals(Arrays.asList(strings));
        request.setDebug(true);

        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new IllegalStateException("Build failed.");
        }
    }

    public void invoke2() throws MavenInvocationException, IOException {

        InvocationRequest request = new DefaultInvocationRequest();
        String[] strings = {
                "dependency:get",
                "--debug",
                "-DrepoUrl=https://repo1.maven.org/maven2",
                "-DgroupId=org.apache.maven.plugin-tools",
                "-DartifactId=maven-plugin-annotations",
                "-Dversion=3.9.0",
        };
        request.setGoals(Arrays.asList(strings));
        request.setDebug(true);
        Invoker invoker = new DefaultInvoker();
//        invoker.setMavenHome(Paths.get(System.getProperty("user.dir")).toFile());
        invoker.setMavenExecutable(Paths.get("/Users/jpanda/.sdkman/candidates/maven/current/bin/mvn").toFile());
//        invoker.setLocalRepositoryDirectory(Paths.get("/Users/jpanda/.m2/repository").toFile());
        invoker.setLocalRepositoryDirectory(Paths.get(System.getProperty("user.dir"), "tmps").toFile());
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new IllegalStateException("Build failed.");
        }

//        String mvnHome = MavenCli.USER_MAVEN_CONFIGURATION_HOME.getAbsolutePath();
//        System.getProperties().setProperty("maven.multiModuleProjectDirectory", mvnHome);
//        MavenCli.main(strings);
    }

}
