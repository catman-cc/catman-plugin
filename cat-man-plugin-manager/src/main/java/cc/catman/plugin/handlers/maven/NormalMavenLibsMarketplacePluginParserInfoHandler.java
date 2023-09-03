package cc.catman.plugin.handlers.maven;

import cc.catman.plugin.common.Constants;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.DescribeConstants;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
// 考虑使用maven-invoke
public class NormalMavenLibsMarketplacePluginParserInfoHandler extends MavenMarketplacePluginParserInfoHandler {

    public static final String LABEL_HC = EDescribeLabel.decorate(NormalMavenLibsMarketplacePluginParserInfoHandler.class.getCanonicalName(), "handler-count");

    public NormalMavenLibsMarketplacePluginParserInfoHandler(MavenOptions mavenOptions) {
        super(mavenOptions);
    }


    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
        // 验证标签信息,第一条,是否有写入类描述文件初始化完成的标签.
        // 第二条,当前存在需要被处理的普通类依赖文件
        return parseInfo.getLabels().exist(EDescribeLabel.DESCRIBE_INFO_FILLED.label())
               && parseInfo.getLabels()
                       .find(EDescribeLabel.EXCLUSIVE_PARSER.label(), DescribeConstants.NEED_PARSER_MAVEN_NORMAL_LIBS_FILE)
                       .isPresent();
    }

    @Override
    public List<String> lifeCycles() {
        return Arrays.asList(ELifeCycle.PRE_LOAD.name());
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        if (parseInfo.getLabels().ge(LABEL_HC, 1)) {
            // 已经被处理过了,所以不能继续处理了
            return true;
        }
        // 读取文件数据
        Resource normalDependencyLibrariesResource = parseInfo.getNormalDependencyLibrariesDescrbieResource();
        if (null == normalDependencyLibrariesResource) {
            // 没有文件,无法解析,这里有一个问题,如果一直无法解析,岂不是会触发递归操作?
            // 所以需要做一个额外的标记,表示自己处理过了
            // 第二次依然无法处理的时候,那就销毁该数据,并推送事件
            parseInfo.getLabels().sum(LABEL_HC, 1);
            return true;
        }
        // 将文件中的内容转换为maven坐标
        try {
            String pomContext = PomFileHelper.readPom(normalDependencyLibrariesResource);
            if (log.isTraceEnabled()){
                log.trace("generator  dependency pom file,content:\n{}",pomContext);
            }
            // 将pom内容写入到插件所属的目录下,然后开始下载插件
            Path pom = Files.write(Paths.get(parseInfo.getBaseDir(), Constants.PLUGIN_MAVEN_NORMAL_DEPENDENCIES_POM_FILE_NAME), pomContext.getBytes());
            log.debug("write third-party dependency data to file :{}",pom);

            Path pluginDir = Paths.get(parseInfo.getBaseDir(), Constants.DEFAULT_NORMAL_DEPENDENCIES_LIBS_DIR);
            Files.createDirectories(pluginDir);
            List<String> list = new ArrayList<>(Arrays.asList(ECommand.DEPENDENCY_COPY_DEPENDENCIES.getCommand(),
                    mavenOptions.isDebug()?"--debug":"",
                    "-f " + pom,
                    "-DoutputDirectory=" + pluginDir));

            InvocationResult result = invoke(parseInfo, list);
            if (result.getExitCode() != 0) {
                throw new RuntimeException(result.getExecutionException());
            }
            // 将插件资源转换为resource
            Files.walkFileTree(pluginDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    parseInfo.getNormalDependencyLibraries().add(new FileSystemResource(file));
                    return FileVisitResult.CONTINUE;
                }

            });
            parseInfo.addOnUninstallFunction((pi) -> {
                if (pluginDir.toFile().delete()) {
                    log.debug("delete the file: {}", pluginDir);
                }
            });
            // 移除标签
            parseInfo.getLabels().rm(EDescribeLabel.EXCLUSIVE_PARSER.label(), DescribeConstants.NEED_PARSER_MAVEN_NORMAL_LIBS_FILE);
            parseInfo.getLabels().add(EDescribeLabel.NORMAL_LIBS_ADDED.label(),getClass().getCanonicalName());
            return true;
        } catch (IOException e) {
            parseInfo.getLabels().rm(LABEL_HC);
            throw new RuntimeException(e);
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
