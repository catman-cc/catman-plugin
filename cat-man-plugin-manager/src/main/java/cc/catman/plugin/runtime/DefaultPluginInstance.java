package cc.catman.plugin.runtime;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.core.IPlugin;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.event.plugin.EPluginEventName;
import cc.catman.plugin.event.plugin.PluginEvent;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.operator.IExtensionPointOperator;
import cc.catman.plugin.options.PluginOptions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.beans.Introspector;
import java.util.*;

@Slf4j
public class DefaultPluginInstance implements IPluginInstance {
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
    protected String group;
    /**
     * 插件的版本信息
     */
    @Getter
    @Setter
    protected String version;

    /**
     * 当前插件的状态
     */
    @Getter
    @Setter
    protected EPluginStatus status;

    /**
     * 创建当前插件实例时,使用到的基础配置数据
     */
    @Getter
    @Setter
    protected PluginParseInfo pluginParseInfo;

    // ===   插件结构 START  ===
    /**
     * 管理当前插件的插件管理器
     */
    @Getter
    protected IPluginManager ownerPluginManager;
    /**
     * 当前插件持有的插件管理器
     */
    @Getter
    protected IPluginManager pluginManager;

    /**
     * 引用了当前插件实例的其他插件实例,或者说,依赖于当前插件的其他插件
     */
    @Getter
    protected Set<IPluginInstance> referencePluginInstance;
    /**
     * 被当前插件引用的其他插件实例,或者说,当前插件依赖的插件
     */
    @Getter
    protected Set<IPluginInstance> usedPluginInstance;
    // ===   插件结构 END  ===

    /**
     * 当前插件用于管理扩展点的 扩展点管理器
     */
    @Getter
    @Setter
    protected IExtensionPointManager extensionPointManager;


    /**
     * 当前插件加载class定义时,所使用的策略
     */
    @Getter
    @Setter
    protected List<String> orderlyClassLoadingStrategy;

    /**
     * 当前插件所持有的特定的插件参数项
     */
    @Getter
    @Setter
    protected PluginOptions pluginOptions;


    public DefaultPluginInstance(IPluginManager ownerPluginManager, PluginParseInfo parseInfo) {
        this.setGroup(parseInfo.getGroup());
        this.setName(parseInfo.getName());
        this.setVersion(parseInfo.getVersion());
        this.setStatus(EPluginStatus.INIT);
        this.pluginParseInfo = parseInfo;

        this.ownerPluginManager = ownerPluginManager;

        this.pluginManager = this.ownerPluginManager.createNew(this, parseInfo.getDependencies());
        this.pluginManager.setOwnerPluginInstance(this);
        this.pluginOptions = ownerPluginManager.getPluginConfiguration().createPluginOptions(this.pluginManager, parseInfo);

        // 加载所以的插件,然后进一步交给插件管理器来处理
        this.pluginManager.setGav(
                GAV.builder()
                        .group(pluginParseInfo.getGroup())
                        .name(pluginParseInfo.getName())
                        .version(pluginParseInfo.getVersion())
                        .build());
        this.referencePluginInstance=new LinkedHashSet<>();
        this.usedPluginInstance=new LinkedHashSet<>();

    }

    /**
     * 获取当前插件用于加载类的 ClassLoader
     */
    @Override
    public ClassLoader getClassLoader() {
        return getPluginParseInfo().getClassLoader();
    }

    @Override
    public void addReference(IPluginInstance instance) {
        this.referencePluginInstance.add(instance);
    }

    @Override
    public void addUsed(IPluginInstance instance){
        this.usedPluginInstance.add(instance);
    }

    /**
     * 更新插件的状态,同时推送状态变更事件
     */
    @Override
    public void updateStatus(EPluginStatus status) {
        if (Objects.equals(getStatus(), status)) {
            return;
        }
        IPluginConfiguration pluginConfiguration = this.getPluginManager().getPluginConfiguration();
        PluginEvent event = PluginEvent.builder()
                .eventName(EPluginEventName.STATUS_CHANGE.name())
                .pluginInstance(this)
                .pluginConfiguration(pluginConfiguration)
                .beforeStatus(getStatus()).build();
        setStatus(status);
        pluginConfiguration.publish(event);
    }


    @Override
    public void start() {
        updateStatus(EPluginStatus.WAIT_DEPENDENCIES_START);
        // 先启动依赖项
        this.pluginManager.start();
        // 然后启动扩展点管理器
        updateStatus(EPluginStatus.WAIT_EXTENSION_POINTS_READY);
        this.extensionPointManager.start();
        // 获取插件对象
        IExtensionPointOperator epv = this.extensionPointManager.createIExtensionPointVisitor();
        List<IPlugin> list=epv.list(IPlugin.class);
        for (IPlugin plugin : list) {
            plugin.afterStart();
        }
        updateStatus(EPluginStatus.START);
    }
    @Override
    public void stop(){
        // 获取插件对象
        IExtensionPointOperator epv = this.extensionPointManager.createIExtensionPointVisitor();
        List<IPlugin> list=epv.list(IPlugin.class);
        for (IPlugin plugin : list) {
            plugin.beforeStop();
        }

        GAV gav = getPluginParseInfo().toGAV();
        log.debug("[{}] is stopping...", gav);
        updateStatus(EPluginStatus.STOPPING);
        // 获取所有扩展点信息,并推送事件,该扩展点将被移除
        log.trace("[{}]`s extension point manager stated stopping...", gav);
        this.extensionPointManager.stop();
        log.trace("[{}]`s extension point manager was stopped...", gav);
        log.trace("[{}]`s plugin manager stated stopping ...", gav);
        // 停止依赖插件
        this.pluginManager.stop();
        log.trace("[{}]`s plugin manager was stopped ...", gav);
        updateStatus(EPluginStatus.STOP);
        log.debug("[{}] was stopped...", gav);
    }

    @Override
    public void uninstall() {
        // 卸载该插件
        stop();
        String gav = getPluginParseInfo().toGAV().toString();
        // 卸载依赖
        usedPluginInstance.forEach(ui->{
            if (ui.getReferencePluginInstance().remove(this)){
                if (ui.getReferencePluginInstance().isEmpty()) {
                    // 插件只被自己引用了,一同卸载
                    log.debug("[{}],uninstall reference plugin:[{}]",gav,ui.getPluginParseInfo().toGAV());
                    ui.uninstall();
                }
            }
        });
        // 交给我管理的插件,不一定是我的依赖,所以不处理
        // 从管理我的容器中移除对我的引用
        log.debug("[{}],remove self-reference from owner plugin manager...", gav);
        ownerPluginManager.getPluginInstances().remove(this);
        // 根据workdir,移除对应的资源,不太好,调用注册的回调方法
        PluginParseInfo parseInfo = getPluginParseInfo();
        log.debug("[{}],call all uninstall callback function", gav);
        parseInfo.callOnUnInstallFunctions(this);
        // 释放资源
        ResourceBundle.clearCache(getClassLoader());
        Introspector.flushCaches();
    }
}
