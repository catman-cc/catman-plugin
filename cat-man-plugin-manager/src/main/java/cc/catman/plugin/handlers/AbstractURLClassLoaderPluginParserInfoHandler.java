package cc.catman.plugin.handlers;

import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.EPluginParserStatus;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;

public abstract class AbstractURLClassLoaderPluginParserInfoHandler extends AbstractPluginParserInfoHandler {

    @SneakyThrows
    protected void build(IParsingProcessProcessor processor,PluginParseInfo parseInfo, List<URL> urls) {
        // 处理扩展依赖数据
        // 处理普通仓库依赖
        processor.registryPluginInstance(parseInfo);
        for (Resource library : parseInfo.getNormalDependencyLibraries()) {
            urls.add(library.getURL());
        }
        Optional.ofNullable(parseInfo.getNormalDependencyLibraries()).ifPresent(libs -> {
            for (Resource lib : libs) {
                try {
                    urls.add(lib.getURL());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        URL[] u = urls.toArray(new URL[]{});
        ClassLoader p = getClass().getClassLoader();
        URLClassLoader classLoader = new URLClassLoader(u, p);
        ConfigurableClassLoaderEnhancer classLoaderEnhancer = parseInfo.getClassLoaderConfiguration().createClassLoaderEnhancer(parseInfo.getPluginInstance());
        // 此处已完成类加载器,然后更新数据
        classLoader = classLoaderEnhancer.wrapper(classLoader, new Class[]{URL[].class, ClassLoader.class}, new Object[]{u, p});
        parseInfo.setStatus(EPluginParserStatus.COMPLETE);
        parseInfo.setClassLoader(classLoader);
        processor.next(parseInfo);
    }
}
