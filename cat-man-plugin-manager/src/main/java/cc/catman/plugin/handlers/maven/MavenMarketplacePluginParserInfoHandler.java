package cc.catman.plugin.handlers.maven;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.handlers.AbstractURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 将MAVEN仓库作为插件市场的实现
 * <p>
 * 如果一个插件,没有工作目录,没有描述文件,那就意味着这个插件需要从市场下载
 */
@Slf4j
public class MavenMarketplacePluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {
    private static final String MAVEN_MARKET_PLACE_NOT_FOUND = "maven-market-place-not-found";
    protected MavenOptions mavenOptions;
    protected Invoker invoker;

    public MavenMarketplacePluginParserInfoHandler(MavenOptions mavenOptions) {
        this.mavenOptions = mavenOptions;
        this.invoker = crateInvoke(mavenOptions);
    }

    @Override
    public List<String> lifeCycles() {
        return Arrays.asList(ELifeCycle.UNKNOWN.name(),ELifeCycle.ACHIEVE.name());
    }

    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
        // 如果一个插件没有描述文件,没有工作目录,那很明显,插件需要由插件市场来处理,那就尝试从maven仓库下载
        return  parseInfo.getLabels().noExist(EDescribeLabel.decorate(MAVEN_MARKET_PLACE_NOT_FOUND))
               && parseInfo.getDescribeResource() == null
               && parseInfo.hasGAV();
    }

    @Override
    @SneakyThrows
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        // 仓库地址
        Path pluginSaveDir = mavenOptions.getPluginStorageStrategy().covert(Paths.get(mavenOptions.getPluginRepositoryDirectory()), parseInfo, true);
        File file = pluginSaveDir.toFile();
        if (file.isDirectory()){
            parseInfo.addOnUninstallFunction((pi) -> {
                if (file.isDirectory()){
                    try {
                        Files.walkFileTree(file.toPath(),new SimpleFileVisitor<Path>(){
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                if (Files.deleteIfExists(file)){
                                    log.debug("delete the file: {}", file);
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                if (Files.deleteIfExists(dir)){
                                    log.debug("delete the file: {}", dir);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
            if (Optional.ofNullable(file.list()).isPresent()){
                List<String> list = new ArrayList<>(Arrays.asList(ECommand.DEPENDENCY_COPY.getCommand(),
                        mavenOptions.isDebug()?"--debug":"",
                        "-Dartifact=" + parseInfo.toGAV().toString(":"),
                        "-DoutputDirectory=" + pluginSaveDir));

                InvocationResult result = invoke(parseInfo, list);
                if (result.getExitCode() != 0) {
                    parseInfo.getLabels().add(EDescribeLabel.decorate(MAVEN_MARKET_PLACE_NOT_FOUND),getClass().getCanonicalName());
                    throw new RuntimeException(result.getExecutionException());
                }
            }else {
                log.debug("{} is not empty dir,skip download plugin...",pluginSaveDir);
            }
        }else {
            log.debug("{} is not dir,skip download plugin...",pluginSaveDir);
        }

        parseInfo.setBaseDir(pluginSaveDir.toString());
        parseInfo.getLabels().rm(EDescribeLabel.EXCLUSIVE_PARSER.label()
                ,EDescribeLabel.NEED_DOWNLOAD_FROM_MARKET_PLACE.label());
        parseInfo.setLifeCycle(ELifeCycle.ACHIEVE.name());
        parseInfo.nextLifeCycle(ELifeCycle.SEARCH.name());

        processor.next(parseInfo);
        return false;
    }


    protected Invoker crateInvoke(MavenOptions options) {
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenExecutable(Paths.get(Optional.ofNullable(options.getMavenExecuteFile()).orElseThrow(() -> new RuntimeException(""))).toFile());

        Optional.ofNullable(options.getMavenHome())
                .ifPresent(mh -> {
                    invoker.setMavenHome(Paths.get(mh).toFile());
                });

        Optional.ofNullable(options.getLocalRepositoryDirectory())
                .ifPresent(lr -> {
                    invoker.setLocalRepositoryDirectory(Paths.get(lr).toFile());
                });

        return invoker;
    }

    public InvocationRequest createInvocationRequestCopy(Invoker invoker, PluginParseInfo mavenPluginParseInfo, List<String> command) {
        InvocationRequest request = new DefaultInvocationRequest();

        Optional.ofNullable(this.mavenOptions.getUserSettingPath()).ifPresent(settings -> {
            request.setUserSettingsFile(Paths.get(settings).toFile());
        });
        Optional.ofNullable(this.mavenOptions.getGlobalSettingPath()).ifPresent(settings -> {
            request.setGlobalSettingsFile(Paths.get(settings).toFile());
        });
        Optional.ofNullable(this.mavenOptions.getJavaHome()).ifPresent(jh -> {
            request.setJavaHome(Paths.get(jh).toFile());
        });

        Optional.ofNullable(this.mavenOptions.getBaseDir()).ifPresent(bd -> {
            request.setBaseDirectory(Paths.get(bd).toFile());
        });
        request.setDebug(this.mavenOptions.isDebug());
        Optional.ofNullable(this.mavenOptions.getRepoUrl())
                .ifPresent(ru -> {
                    command.add("-DrepoUrl=" + ru);
                });
        request.setGoals(command);

        return request;
    }

    protected InvocationRequest beforeInvocationRequestExec(InvocationRequest request, Invoker invoker, PluginParseInfo mavenPluginParseInfo) {
        return request;
    }

    protected InvocationResult invoke(PluginParseInfo mppi, List<String> command) throws MavenInvocationException {
        InvocationRequest request = createInvocationRequestCopy(this.invoker, mppi, command);
        request = beforeInvocationRequestExec(request, this.invoker, mppi);
        return this.invoker.execute(request);
    }

}
