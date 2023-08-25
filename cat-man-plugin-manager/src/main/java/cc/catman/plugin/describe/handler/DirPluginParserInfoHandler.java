package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.DirPluginDescribe;
import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DirPluginParserInfoHandler extends AbstractPluginParserInfoHandler{

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

        PluginDescribe pd=parseInfo.getPluginDescribe();

        final DirPluginDescribe dpd=DirPluginDescribe.class.isAssignableFrom(pd.getClass())
                ?(DirPluginDescribe) pd
                :parseInfo.decode(DirPluginDescribe.class);

        return dpd.getDirs().stream().map(Paths::get).map(basedir -> {
            List<PluginDescribe> pluginDescribes = new ArrayList<>();
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
                            // 这里表示名称符合要求,那么将会被转换为基础的插件描述信息
                            PluginDescribe pd = new PluginDescribe();
                            pd.setResource(new FileSystemResource(file.toFile()));
                            pluginDescribes.add(pd);
                            // 如果在一个目录下找到了一个插件,那么就会跳过该文件的同级目录及其子目录,理论上,插件应该不会出现多级嵌套,所以这里这么处理应该没问题.
                            return FileVisitResult.SKIP_SIBLINGS;

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
