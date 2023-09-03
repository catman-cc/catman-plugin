package cc.catman.plugin.extensionPoint;

import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEvent;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEventInfoName;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEventName;
import cc.catman.plugin.event.extensionPoint.ExtensionPointInfoEvent;
import cc.catman.plugin.extensionPoint.finder.IExtensionPointFinder;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.operator.DefaultExtensionOperator;
import cc.catman.plugin.operator.IExtensionPointOperator;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultExtensionPointManager implements IExtensionPointManager {
    @Getter
    protected IPluginInstance pluginInstance;
    @Getter
    protected List<ExtensionPointInfo> extensionPointInfos;
    @Getter
    protected List<IExtensionPointFinder> extensionPointFinders;

    @Getter
    protected IExtensionPointInstanceFactory extensionPointInstanceFactory;

    public DefaultExtensionPointManager(IPluginInstance pluginInstance) {
        this.pluginInstance = pluginInstance;
        pluginInstance.setExtensionPointManager(this);
        this.extensionPointFinders = new ArrayList<>();
        this.extensionPointInfos = new ArrayList<>();
        this.extensionPointInstanceFactory = pluginInstance
                .getPluginManager()
                .getPluginConfiguration().createExtensionPointInstanceFactory(this);
    }

    @Override
    public IExtensionPointOperator createIExtensionPointVisitor() {
        return new DefaultExtensionOperator(this);
    }

    @SneakyThrows
    public void start() {
        // 将扩展点信息写入当前列表
        registryExtensionPoints(pluginInstance.getPluginParseInfo());
    }

    @Override
    public void stop() {
        IPluginConfiguration pluginConfiguration = pluginInstance.getPluginManager().getPluginConfiguration();
        extensionPointInfos.forEach(extensionPointInfo -> {
            // 获取所有扩展点信息,并推送事件,该扩展点将被移除,需要监听方合理的处理正在使用的扩展点实例
            // 同时因为这的通知是同步,所以监听者可以合理的hung起服务,直到业务停止扩展点的使用
            extensionPointInfo.setValid(false);
            pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventName.WILL_STOP.name(), this,extensionPointInfo));
        });
    }

    private void registryExtensionPoints(StandardPluginDescribe standardPluginDescribe) {
        IPluginConfiguration pluginConfiguration = pluginInstance.getPluginManager().getPluginConfiguration();
        // 推送事件,开始加载扩展点
        pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventName.FIND_EXTENSION_POINT_START.name(), this));


        // 此处开始注册所有的扩展点信息
        Map<String, ExtensionPointInfo> cache = new HashMap<>();
        // 如果直接简单的将所有的extensionPointInfo存放到集合中,可能会导致extensionPointInfo被多次声明,因此,还需要执行合并和去重操作.
        extensionPointFinders.stream()
                .flatMap(finder -> finder.find(standardPluginDescribe))
                .filter(extensionPointInfo -> !cache.containsKey(extensionPointInfo.getClassName()))
                .forEach(extensionPointInfo -> {
                    log.debug("Find a new extension point definition:{}",extensionPointInfo);
                    this.extensionPointInfos.add(extensionPointInfo);
                    pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventName.FOUND.name(), this, extensionPointInfo));
                    cache.put(extensionPointInfo.getClassName(), extensionPointInfo);
                    pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventInfoName.ADD.name(), this, extensionPointInfo));
                });
        // 推送事件,扩展点加载完毕
        pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventName.FIND_EXTENSION_POINT_END.name(), this));
    }

}
