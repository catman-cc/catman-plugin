package cc.catman.plugin.operator;

import cc.catman.plugin.event.extensionPoint.ExtensionPointEventName;
import cc.catman.plugin.event.extensionPoint.ExtensionPointInfoEvent;
import cc.catman.plugin.event.extensionPoint.WatchExtensionPointEventListener;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointInstanceFactory;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.runtime.EPluginStatus;
import cc.catman.plugin.runtime.IPluginConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultExtensionOperator implements IExtensionPointOperator {
    protected IExtensionPointManager extensionPointManager;

    protected IExtensionPointInstanceFactory extensionPointInstanceFactory;

    protected List<ExtensionPointInfo> extensionPointInfos;

    protected ExtensionPointOperatorOptions options;

    public DefaultExtensionOperator(IExtensionPointManager extensionPointManager, ExtensionPointOperatorOptions options) {
        this.extensionPointManager = extensionPointManager;
        this.extensionPointInstanceFactory=extensionPointManager.getExtensionPointInstanceFactory();
        this.extensionPointInfos=extensionPointManager.getExtensionPointInfos();
        this.options = options;
    }

    public DefaultExtensionOperator(IExtensionPointManager extensionPointManager) {
        this.extensionPointManager = extensionPointManager;
        this.extensionPointInstanceFactory=extensionPointManager.getExtensionPointInstanceFactory();
        this.extensionPointInfos=extensionPointManager.getExtensionPointInfos();
        this.options= ExtensionPointOperatorOptions.builder().build();
    }

    @Override
    public <T> List<T> list(Class<T> type, Predicate<ExtensionPointInfo> beforeInstance, Predicate<T> afterInstance) {
        return list(type,beforeInstance,afterInstance,Optional.empty());
    }

    @Override
    public <T> List<T> list(Class<T> type, Predicate<ExtensionPointInfo> beforeInstance, Predicate<T> afterInstance, Optional<WatchExtensionPointEventListener> watch) {
        return listWithInstanceFactory(type,(c)-> this.extensionPointInstanceFactory.newInstance(c),beforeInstance,afterInstance,watch);
    }

    @Override
    public <T> List<T> listWithInstanceFactory(Class<T> type, Function<Class<T>,T > factory, Predicate<ExtensionPointInfo> beforeInstance, Predicate<T> afterInstance) {
        return listWithInstanceFactory(type,factory,beforeInstance, afterInstance, Optional.empty());
    }

    @Override
    public <T> List<T> listWithInstanceFactory(Class<T> type, Function<Class<T>, T> factory, Predicate<ExtensionPointInfo> beforeInstance, Predicate<T> afterInstance,Optional<WatchExtensionPointEventListener> watch) {
        return this.streamWithInstanceFactory(type,factory,beforeInstance,watch)
                .filter(afterInstance)
                .collect(Collectors.toList());
    }


    @Override
    public <T> Stream<T> stream(Class<T> type, Predicate<ExtensionPointInfo> filter) {
        return streamWithInstanceFactory(type,(c)-> this.extensionPointInstanceFactory.newInstance(c),filter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Stream<T> streamWithInstanceFactory(Class<T> type, Function<Class<T>, T> factory, Predicate<ExtensionPointInfo> filter) {
       return streamWithInstanceFactory(type,factory,filter,Optional.empty());
    }

    @Override
    public <T> Stream<T> streamWithInstanceFactory(Class<T> type, Function<Class<T>, T> factory, Predicate<ExtensionPointInfo> filter, Optional<WatchExtensionPointEventListener> watchExtensionPointEventListener) {
        if (isStopping()){
            return Stream.empty();
        }
        return extensionPointInfos.stream()
                .filter(options.wrapper(filter))
                .map(extensionPointInfo-> {
                    Class<T> clazz = (Class<T>) extensionPointInfo.getClazz();
                    if (!type.isAssignableFrom(clazz)){
                        return null;
                    }
                    watchExtensionPointEventListener.ifPresent(w->{
                        w.add(extensionPointInfo);
                    });
                    T t =  factory.apply(clazz);
                    IPluginConfiguration pluginConfiguration = extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration();
                    pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventName.INSTANCE.name(), extensionPointManager, extensionPointInfo,t));
                    return t;
                }).filter(Objects::nonNull);
    }

    @Override
    public <T> List<Class<T>> listClazz(Class<T> type, Predicate<ExtensionPointInfo> filter) {
        return streamClasses(type,filter).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Stream<Class<T>> streamClasses(Class<T> type, Predicate<ExtensionPointInfo> filter) {
        if (isStopping()){
            return Stream.empty();
        }
        return extensionPointInfos.stream()
                .filter(options.wrapper(filter))
                .map(extensionPointInfo-> (Class<T>)extensionPointInfo.getClazz());
    }


    @Override
    public List<Class<?>> list() {
        return stream().collect(Collectors.toList());
    }

    @Override
    public  Stream<Class<?>> stream() {
        if (isStopping()){
            return Stream.empty();
        }
        return this.extensionPointInfos.stream().filter(options.wrapper()).map(ExtensionPointInfo::getClazz);
    }

    @Override
    public <T> Optional<T> first(Class<T> type) {
        return stream(type).findFirst();
    }

    @Override
    public <T> Optional<Class<T>> firstClass(Class<T> type) {
        return streamClasses(type).findFirst();
    }

    @Override
    public void update(Predicate<ExtensionPointInfo> filter, Consumer<ExtensionPointInfo> updater) {
        if (isStopping()){
            return;
        }
        extensionPointInfos.stream().filter(options.wrapper(filter)).forEach(epi->{
            ExtensionPointInfo old = epi.deepNew();
            updater.accept(epi);
            IPluginConfiguration pluginConfiguration = extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration();
            pluginConfiguration.publish(ExtensionPointInfoEvent.of(ExtensionPointEventName.CHANGE.name(), extensionPointManager, old,epi));
        });
    }

    protected boolean isStopping(){
        EPluginStatus status = extensionPointManager.getPluginInstance().getStatus();
        return  EPluginStatus.DISABLED.equals(status)||EPluginStatus.STOP.equals(status)||EPluginStatus.STOPPING.equals(status);
    }
}
