package cc.catman.plugin.describe.handler.mvn;

import cc.catman.plugin.common.Constants;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.*;
import cc.catman.plugin.describe.handler.maven.ECommand;
import cc.catman.plugin.describe.handler.maven.MavenDownloadPluginParserInfoHandler;
import cc.catman.plugin.describe.handler.maven.MavenOptions;
import org.apache.maven.shared.invoker.*;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

// 考虑使用maven-invoke
public class NormalMavenLibsDownloadPluginParserInfoHandler extends MavenDownloadPluginParserInfoHandler {

    public static final String SUPPORT_SOURCE = EPluginSource.LOCAL.name();
    public static final String SUPPORT_KIND = EPluginKind.MAVEN.name();
    public static final String LABEL_HC = EDescribeLabel.decorate(NormalMavenLibsDownloadPluginParserInfoHandler.class.getCanonicalName(), "handler-count");

    public NormalMavenLibsDownloadPluginParserInfoHandler(MavenOptions mavenOptions) {
        super(mavenOptions);
    }


    @Override
    public boolean support(PluginParseInfo parseInfo) {
        // 验证标签信息,第一条,是否有写入类描述文件初始化完成的标签.
        // 第二条,当前存在需要被处理的普通类依赖文件
        return parseInfo.getLabels().exist(EDescribeLabel.DESCRIBE_FILE_PARSED.label())
               && parseInfo.getLabels()
                       .pop(EDescribeLabel.EXCLUSIVE_PARSER.label(), DescribeConstants.NEED_PARSER_MAVEN_NORMAL_LIBS_FILE)
                       .isPresent();
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
        if (parseInfo.getLabels().ge(LABEL_HC, 1)) {
            // 已经被处理过了,所以不能继续处理了
            // TODO 推送一个插件描述被丢弃的事件
            return Collections.emptyList();
        }
        // 读取文件数据
        Resource normalDependencyLibrariesResource = parseInfo.getNormalDependencyLibrariesDescrbieResource();
        if (null == normalDependencyLibrariesResource) {
            // 没有文件,无法解析,这里有一个问题,如果一直无法解析,岂不是会触发递归操作?
            // 所以需要做一个额外的标记,表示自己处理过了
            // 第二次依然无法处理的时候,那就销毁该数据,并推送事件
            parseInfo.getLabels().sum(LABEL_HC, 1);
            return Collections.singletonList(parseInfo);
        }
        // 将文件中的内容转换为maven坐标
        try {
            String pomContext = PomFileHelper.readPom(normalDependencyLibrariesResource);
            // 将pom内容写入到插件所属的目录下,然后开始下载插件
            Path pom = Files.write(Paths.get(parseInfo.getBaseDir(), Constants.PLUGIN_MAVEN_NORMAL_DEPENDENCIES_POM_FILE_NAME), pomContext.getBytes());
            Path pluginDir = Paths.get(parseInfo.getBaseDir(), "libs");
            Files.createDirectories(pluginDir);
            List<String> list = new ArrayList<>(Arrays.asList(ECommand.DEPENDENCY_COPY_DEPENDENCIES.getCommand(),
                    "--debug",
                    "-f " + pom,
                    "-DoutputDirectory=" + pluginDir));

            InvocationResult result = invoke(parseInfo, list);
            if (result.getExitCode() != 0) {
                // TODO 异常


            }
            // 将插件资源转换为resource
            Files.walkFileTree(pluginDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    parseInfo.getNormalDependencyLibraries().add(new FileSystemResource(file));
                    return FileVisitResult.CONTINUE;
                }

            });
            // 移除标签
            parseInfo.getLabels().rm(EDescribeLabel.EXCLUSIVE_PARSER.label(), DescribeConstants.NEED_PARSER_MAVEN_NORMAL_LIBS_FILE);
            parseInfo.getLabels().add(EDescribeLabel.NORMAL_LIBS_ADDED.label(),getClass().getCanonicalName());
            return Collections.singletonList(parseInfo);
        } catch (IOException e) {
            // TODO 资源文件读取失败,表示这是错误的数据,不应该被俺处理,移除标签,避免死循环
            parseInfo.getLabels().rm(LABEL_HC);
            // TODO 推送事件
            throw new RuntimeException(e);
        } catch (MavenInvocationException e) {
            throw new RuntimeException(e);
        }
    }
}
