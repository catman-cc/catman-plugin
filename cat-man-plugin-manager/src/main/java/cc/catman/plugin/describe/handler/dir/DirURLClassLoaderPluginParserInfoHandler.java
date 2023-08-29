package cc.catman.plugin.describe.handler.dir;

import cc.catman.plugin.describe.handler.AbstractURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import cc.catman.plugin.describe.enmu.EPluginSource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 目录类型的插件信息,用于提供一组插件
 */
public class DirURLClassLoaderPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {

    @Override
    protected List<String> getKinds() {
        return Collections.singletonList(EPluginKind.DIR.name());
    }

    @Override
    protected List<String> getSources() {
        return Collections.singletonList(EPluginSource.LOCAL.name());
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {

        final DirPluginParseInfo dpd= DirPluginParseInfo.class.isAssignableFrom(parseInfo.getClass())
                ?(DirPluginParseInfo) parseInfo
                :parseInfo.decode(DirPluginParseInfo.class);

        return dpd.getDirs().stream().map(Paths::get).map(basedir -> {
            List<PluginParseInfo> pluginDescribes = new ArrayList<>();
            try {
                Files.walkFileTree(basedir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        // 理论上这里只需要得到一个文件即可,找到符合要求的文件
                        // 目录
                        if (file.toFile().isDirectory()) {
                            return FileVisitResult.CONTINUE;
                        }

                        String filename = file.getFileName().toString();
                        if (dpd.getSupportPluginDescFileNames().stream().anyMatch(filename::startsWith)) {
                            // 这里表示名称符合要求,那么将会被转换为基础的插件描述信息,但该插件信息仅包含资源文件的名称
                            // 具体的内容需要交给解析器去解析

                            PluginParseInfo pd = PluginParseInfo.builder()
                                    .status(EPluginParserStatus.WAIT_PARSE)
                                    .describeResource(new FileSystemResource(file.toFile()))
                                    .build();
                            // 复制插件描述信息.同时需要为当前pd插入详细的描述信息
                            pluginDescribes.add(pd);
                            return FileVisitResult.CONTINUE;

                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return pluginDescribes;
        }).flatMap(
                npds -> npds.stream().flatMap(
                        npd -> parseInfo.getPluginParserContext().parser(npd).stream()
                        )
                )
                .collect(Collectors.toList());
    }
}
