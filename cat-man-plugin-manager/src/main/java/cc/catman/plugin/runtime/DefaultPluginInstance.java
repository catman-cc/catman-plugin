package cc.catman.plugin.runtime;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;

public class DefaultPluginInstance implements IPluginInstance{

    @Getter
    @Setter
    protected ClassLoader classLoader;
    @Getter
    @Setter
    protected PluginParseInfo pluginParseInfo;
    @Getter
    protected IPluginManager ownerPluginManager;
    @Getter
    protected IPluginManager pluginManager;

    @Getter
    @Setter
    protected IExtensionPointManager extensionPointManager;
    @Getter
    @Setter
    protected List<String> orderlyClassLoadingStrategy;

    public DefaultPluginInstance(IPluginManager ownerPluginManager,PluginParseInfo parseInfo) {
        this.ownerPluginManager=ownerPluginManager;
        this.pluginParseInfo = parseInfo;
        this.classLoader = parseInfo.getClassLoader();
        // 加载所以的插件,然后进一步交给插件管理器来处理
        this.pluginManager=this.ownerPluginManager.createNew(parseInfo.getPluginDescribe().getSystemDependencies(),parseInfo.getPluginDescribe().getDependencies());


    }

    @Override
    @SneakyThrows
    public Class<?> deepFindClass(String name,int deep) {
        if (deep==0){
            // 不能继续往下找了,直接从自己本身找就可以了
           return classLoader.loadClass(name);
        }
        // 当前插件寻找类定义
        return pluginManager.deepFindClass(name,--deep);
    }

    @Override
    public void start() {
        // 先启动依赖项
        this.pluginManager.start();
        // 然后启动扩展点管理器
        this.extensionPointManager.start();
    }
}
