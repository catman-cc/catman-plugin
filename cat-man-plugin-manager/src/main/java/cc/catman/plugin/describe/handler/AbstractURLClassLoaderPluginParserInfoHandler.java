package cc.catman.plugin.describe.handler;

import cc.catman.plugin.classloader.cglib.ConfigurableClassLoaderEnhancer;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;

public abstract class AbstractURLClassLoaderPluginParserInfoHandler extends AbstractPluginParserInfoHandler{


    protected List<PluginParseInfo> build(PluginParseInfo parseInfo, List<URL> urls) {
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
