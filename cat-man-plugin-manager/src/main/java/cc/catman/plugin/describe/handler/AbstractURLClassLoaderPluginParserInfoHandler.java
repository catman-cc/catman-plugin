package cc.catman.plugin.describe.handler;

import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractURLClassLoaderPluginParserInfoHandler extends AbstractPluginParserInfoHandler{


    protected List<PluginParseInfo> build(PluginParseInfo parseInfo, List<URL> urls) {
        // 处理扩展依赖数据
        Optional.ofNullable(parseInfo.getNormalDependencyLibraries()).ifPresent(libs->{
            for (Resource lib : libs) {
                try {
                    urls.add(lib.getURL());
                } catch (IOException e) {
                    // TODO  推送事件, 处理过程中发生异常,无法获取第三方库的URL信息
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
        return Collections.singletonList(parseInfo);
    }
}
