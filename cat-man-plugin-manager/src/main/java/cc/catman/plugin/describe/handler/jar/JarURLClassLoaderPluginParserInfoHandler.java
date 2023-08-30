package cc.catman.plugin.describe.handler.jar;

import cc.catman.plugin.describe.handler.AbstractURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import lombok.SneakyThrows;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于解析jar类型的插件
 */
public class JarURLClassLoaderPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {
    public static final String SUPPORT_SOURCE = EPluginSource.LOCAL.name();
    public static final String SUPPORT_KIND = EPluginKind.JAR.name();

    @Override
    protected List<String> getKinds() {
        return Collections.singletonList(SUPPORT_KIND);
    }

    @Override
    protected List<String> getSources() {
        return Collections.singletonList(SUPPORT_SOURCE);
    }

    @Override
    @SneakyThrows
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        // 获取相对路径,得到项目的跟地址,然后加载其中的所有资源,最后生成一个URLClassLoader
        Path workDir = parseInfo.getDescribeResource().getFile().toPath().resolveSibling(parseInfo.getRelativePath());

        final JarPluginParseInfo jpd = covert(parseInfo, JarPluginParseInfo.class);
        // 第一次加载,所使用的描述文件可能需要被插件内部的描述文件所替换,是否替换根据当前插件描述文件来控制


        // 获取了一个jar类型的插件描述信息

        // 支持ant语法
        AntPathMatcher pathMatcher = new AntPathMatcher();
        List<URL> urls = new ArrayList<>();
        Files.walkFileTree(workDir, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String v = workDir.relativize(dir).toString();
                return jpd.getLibAntPatterns().stream().anyMatch(p -> pathMatcher.matchStart(p,v ))
                        ? FileVisitResult.CONTINUE
                        : FileVisitResult.SKIP_SUBTREE
                        ;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws MalformedURLException {
                // 然后就是解析jar包了,其实无论是jar包还是war包,应该问题都不大.
                String filename = file.getFileName().toString();
                if (filename.endsWith("jar") || filename.endsWith("war")) {
                    urls.add(file.toUri().toURL());
                }

                return FileVisitResult.CONTINUE;
            }
        });
        return build(jpd, urls);
    }
}
