package cc.catman.plugin.extensionPoint;

import cc.catman.plugin.event.extensionPoint.ExtensionPointEvent;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEventInfoName;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEventName;
import cc.catman.plugin.event.extensionPoint.ExtensionPointInfoEvent;
import cc.catman.plugin.extensionPoint.finder.IExtensionPointFinder;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.describe.PluginDescribe;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultExtensionPointManager implements IExtensionPointManager {
    @Getter
    protected IPluginInstance pluginInstance;
    @Getter
    protected List<ExtensionPointInfo> extensionPointInfos;
    @Getter
    protected List<IExtensionPointFinder> extensionPointFinders;

    @Getter
    protected IExtensionPointInstanceFactory extensionPointInstanceFactory;


    private IExtensionPointInstanceFactory createExtensionPointInstanceFactory() {
        return new DefaultExtensionPointInstanceFactory(this);
    }

    public DefaultExtensionPointManager(IPluginInstance pluginInstance) {
        this.pluginInstance = pluginInstance;
        pluginInstance.setExtensionPointManager(this);
        this.extensionPointFinders = new ArrayList<>();
        this.extensionPointInfos = new ArrayList<>();
        this.extensionPointInstanceFactory = createExtensionPointInstanceFactory();
    }

    @SneakyThrows
    public void start() {
        // 将扩展点信息写入当前列表
        registryExtensionPoints(pluginInstance.getPluginParseInfo().getPluginDescribe());
    }

    private void registryExtensionPoints(PluginDescribe pluginDescribe) {
        IPluginConfiguration pluginConfiguration = pluginInstance.getPluginManager().getPluginConfiguration();
        // 推送事件,开始加载扩展点
        pluginConfiguration.publish(ExtensionPointEvent.of(ExtensionPointEventName.FIND_EXTENSION_POINT_START.name(),this));


        // 此处开始注册所有的扩展点信息
        Map<String, ExtensionPointInfo> cache = new HashMap<>();
        // 如果直接简单的将所有的extensionPointInfo存放到集合中,可能会导致extensionPointInfo被多次声明,因此,还需要执行合并和去重操作.
        extensionPointFinders.stream()
                .flatMap(finder -> finder.find(pluginDescribe))
                .forEach(extensionPointInfo -> {
                    if (cache.containsKey(extensionPointInfo.getClassName())) {
                        // 已经包含了当前定义,那就执行合并操作
                        cache.get(extensionPointInfo.getClassName()).merge(extensionPointInfo);
                        pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventInfoName.MERGE.name(),this,extensionPointInfo));
                    } else {
                        this.extensionPointInfos.add(extensionPointInfo);
                        cache.put(extensionPointInfo.getClassName(), extensionPointInfo);
                        pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventInfoName.ADD.name(),this,extensionPointInfo));
                    }

                });
        // 推送事件,扩展点加载完毕
        pluginConfiguration.publish(ExtensionPointEvent.of(ExtensionPointEventName.FIND_EXTENSION_POINT_END.name(),this));
    }

//    protected ExtensionPointInfo cache(String name, Class<?> clazz, Class<?> wantType) {
//        ExtensionPointInfo extensionPointInfo = this.extensionPointInfos.stream().filter(epi -> epi.getClassName().equals(name)).findFirst().orElseGet(() -> {
//            ExtensionPointInfo e = ExtensionPointInfo
//                    .builder()
//                    .className(StringUtils.hasText(name) ? name : clazz.getName())
//                    .clazz(clazz)
//                    .build();
//            extensionPointInfos.add(e);
//            return e;
//        });
//        if (wantType != null) {
//            Set<Class<?>> supportTypes = extensionPointInfo.getSupportTypes();
//            if (!supportTypes.contains(wantType)) {
//                supportTypes.add(wantType);
//            }
//        }
//        return extensionPointInfo;
//    }
//
    public void toggle(String name) {
        this.extensionPointInfos.forEach(epi -> {
            if (epi.getClassName().equals(name)) {
                epi.setDisable(!epi.isDisable());
            }
        });
    }

    public void disable(String name, boolean disable) {
        this.extensionPointInfos.forEach(epi -> {
            if (epi.getClassName().equals(name)) {
                epi.setDisable(disable);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> findFirst(Class<T> type) {
        Class<?> c = listClass(type).stream().findFirst().orElse(null);
        if (c == null) {
            return Optional.empty();
        }
        return (Optional<T>) Optional.ofNullable(this.extensionPointInstanceFactory.newInstance(c));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<T> type) {
        // 使用查找器进行查找,同时将查找到的定义,使用校验器验证,每一个查找器和验证器都是和插件一一对应的,所以可以具有缓存
        // 通过finder+matcher的组合,可以将一些非标准扩展点进行额外的标记
        return (List<T>) this.listClass(type)
                .stream()
                .map(c -> this.extensionPointInstanceFactory.newInstance(c)).collect(Collectors.toList());
    }

    public List<Class<?>> listClass(Class<?> type) {
        return listClass(type, false);
    }

    public List<Class<?>> listClass(Class<?> type, boolean includeDisabled) {
        List<Class<?>> cs = extensionPointInfos.stream()
                .filter(epi -> includeDisabled
                        ? epi.getSupportTypes().contains(type)
                        : (!epi.isDisable()) && epi.getSupportTypes().contains(type))
                .map(ExtensionPointInfo::getClazz)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(cs)) {
            return cs;
        }
        return Collections.emptyList();
        //TODO
//        return this.extensionPointFinders.stream()
//                .flatMap(finder-> finder.find()
//                        .map(c-> this.extensionPointMatchers
//                                .stream()
//                                .map(m-> m.match(c,type))
//                                .filter(Objects::nonNull)
//                                .findFirst())
//                        .filter(Optional::isPresent)
//                        .map(Optional::get))
//                .distinct()
//                .filter(c->{
//                    ExtensionPointInfo cache = cache("", c, type);
//                    if (includeDisabled){
//                        return true;
//                    }
//                    return !cache.isDisable();
//                })
//                .collect(Collectors.toList());
    }


}
