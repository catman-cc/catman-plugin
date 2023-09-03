package cc.catman.plugin.handlers.jar;

import cc.catman.plugin.common.Mapper;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.EPluginKind;
import cc.catman.plugin.enums.EPluginSource;
import cc.catman.plugin.handlers.AbstractURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 用于解析jar类型的插件
 */
@Slf4j
public class JarURLClassLoaderPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {
    @Override
    public List<String> lifeCycles() {
        return Arrays.asList(ELifeCycle.LOAD.name());
    }

    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
        return parseInfo.hasGAV()
               && Optional.ofNullable(parseInfo.getDescribeResource()).isPresent()
               && Optional.ofNullable(parseInfo.getBaseDir()).isPresent();
    }

    @Override
    @SneakyThrows
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        // 获取相对路径,得到项目的跟地址,然后加载其中的所有资源,最后生成一个URLClassLoader
        Path workDir = (parseInfo.getDescribeResource().isFile()
                ? parseInfo.getDescribeResource().getFile().toPath().resolve(parseInfo.getRelativePath())
                : Paths.get(parseInfo.getBaseDir())).normalize();

        // 第一次加载,所使用的描述文件可能需要被插件内部的描述文件所替换,是否替换根据当前插件描述文件来控制
        final JarPluginParseInfo jpd = Mapper.map(parseInfo, JarPluginParseInfo.class, "libAntPatterns");
        AntPathMatcher pathMatcher = new AntPathMatcher();
        List<URL> urls = new ArrayList<>();
        Files.walkFileTree(workDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                String v = workDir.relativize(dir).toString();
                return jpd.getLibAntPatterns().stream().anyMatch(p -> pathMatcher.matchStart(p, v))
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
        // 注册一个卸载时执行的任务
        parseInfo.addOnUninstallFunction((pi) -> {
            urls.forEach(url -> {
                UrlResource urlResource = new UrlResource(url);
                if (urlResource.isFile()) {
                    try {
                        if (urlResource.getFile().delete()) {
                            log.debug("delete the file: {}", url);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
        build(processor, jpd, urls);

        return false;
    }


}
