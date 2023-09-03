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
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.security.ProtectionDomain;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
    @Getter
    @Setter
    protected EPluginStatus status;

    @Getter
    @Setter
    protected PluginParseInfo pluginParseInfo;
    @Getter
    protected IPluginManager ownerPluginManager;
    @Getter
    protected IPluginManager pluginManager;

    @Getter
    protected Set<IPluginInstance> referencePluginInstance;
    @Getter
    protected Set<IPluginInstance> usedPluginInstance;

    @Getter
    @Setter
    protected IExtensionPointManager extensionPointManager;
    @Getter
    @Setter
    protected List<String> orderlyClassLoadingStrategy;
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

    @Override
    public void updateStatus(EPluginStatus status) {
        if (getStatus().equals(status)) {
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
        IExtensionPointOperator iExtensionPointVisitor = this.extensionPointManager.createIExtensionPointVisitor();
        List<IPlugin> list = iExtensionPointVisitor.list(IPlugin.class);
        for (IPlugin plugin : list) {
            plugin.onload();
        }
        updateStatus(EPluginStatus.START);
    }
    @Override
    public void stop(){
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
