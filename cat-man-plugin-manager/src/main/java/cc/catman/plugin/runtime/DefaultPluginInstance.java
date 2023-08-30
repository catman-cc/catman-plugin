package cc.catman.plugin.runtime;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.options.PluginOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;

public class DefaultPluginInstance implements IPluginInstance{
    /**
     * 插件名称
     */
    @Getter
    @Setter
    protected String name;
    /**
     * 插件所属组织
     */
    @Getter
    @Setter
    protected  String group;
    /**
     * 插件的版本信息
     */
    @Getter
    @Setter
    protected String version;
    @Getter
    @Setter
    protected EPluginStatus status;

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
    @Getter
    @Setter
    protected PluginOptions pluginOptions;
    public DefaultPluginInstance(IPluginManager ownerPluginManager, PluginParseInfo parseInfo,PluginOptions options) {
        this.setGroup(parseInfo.getGroup());
        this.setName(parseInfo.getName());
        this.setVersion(parseInfo.getVersion());
        this.setStatus(EPluginStatus.INIT);
        this.ownerPluginManager=ownerPluginManager;
        this.pluginOptions=options;
        this.pluginParseInfo = parseInfo;
        this.classLoader = parseInfo.getClassLoader();
        // 加载所以的插件,然后进一步交给插件管理器来处理
        this.pluginManager=this.ownerPluginManager.createNew(parseInfo.getDependencies());
        this.pluginManager.setOwnerPluginInstance(this);
        this.pluginManager.setGav(
                GAV.builder()
                        .group(pluginParseInfo.getGroup())
                        .name(pluginParseInfo.getName())
                        .version(pluginParseInfo.getVersion())
                .build());

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
        this.setStatus(EPluginStatus.WAIT_DEPENDENCIES);
        // 先启动依赖项
        this.pluginManager.start();
        // 然后启动扩展点管理器
        this.extensionPointManager.start();
    }
}
