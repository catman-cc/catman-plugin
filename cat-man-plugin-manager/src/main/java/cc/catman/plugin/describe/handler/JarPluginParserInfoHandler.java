package cc.catman.plugin.describe.handler;

import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import cc.catman.plugin.describe.JarPluginDescribe;
import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import lombok.SneakyThrows;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JarPluginParserInfoHandler extends AbstractPluginParserInfoHandler {
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
        PluginDescribe pd = parseInfo.getPluginDescribe();

        final JarPluginDescribe jpd = JarPluginDescribe.class.isAssignableFrom(pd.getClass())
                ? (JarPluginDescribe) pd
                : parseInfo.decode(JarPluginDescribe.class);

        // 获取了一个jar类型的插件描述信息
        // 获取相对路径,得到项目的跟地址,然后加载其中的所有资源,最后生成一个URLClassLoader
        Path workDir = jpd.getResource().getFile().toPath().resolveSibling(jpd.getRelativePath());
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
        URL[] u = urls.toArray(new URL[]{});
        ClassLoader p = getClass().getClassLoader();
        URLClassLoader classLoader = new URLClassLoader(u, p);
        ConfigurableClassLoaderEnhancer classLoaderEnhancer = parseInfo.getClassLoaderConfiguration().createClassLoaderEnhancer(parseInfo.getPluginInstance());
        // 此处已完成类加载器,然后更新数据
        classLoader = classLoaderEnhancer.wrapper(classLoader, new Class[]{URL[].class, ClassLoader.class}, new Object[]{u, p});
        parseInfo.setPluginDescribe(jpd);
        parseInfo.setStatus(EPluginParserStatus.SUCCESS);
        parseInfo.setClassLoader(classLoader);
        return Collections.singletonList(parseInfo);
    }
}
