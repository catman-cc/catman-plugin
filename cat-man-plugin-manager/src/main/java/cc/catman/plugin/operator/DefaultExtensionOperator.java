package cc.catman.plugin.operator;

import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointInstanceFactory;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;

import java.util.List;
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
        return listWithInstanceFactory(type,(c)-> this.extensionPointInstanceFactory.newInstance(c),beforeInstance,afterInstance);
    }

    @Override
    public <T> List<T> listWithInstanceFactory(Class<T> type, Function<Class<T>,T > factory, Predicate<ExtensionPointInfo> beforeInstance, Predicate<T> afterInstance) {
        return this.streamClasses(type,beforeInstance)
                .map(factory)
                .filter(afterInstance)
                .collect(Collectors.toList());
    }

    @Override
    public <T> Stream<T> stream(Class<T> type, Predicate<ExtensionPointInfo> filter) {
        return streamClasses(type,filter).map((c)-> this.extensionPointInstanceFactory.newInstance(c));
    }

    @Override
    public <T> Stream<T> streamWithInstanceFactory(Class<T> type, Function<Class<T>, T> factory, Predicate<ExtensionPointInfo> filter) {
        return this.streamClasses(type,filter)
                .map(factory);
    }

    @Override
    public <T> List<Class<T>> listClazz(Class<T> type, Predicate<ExtensionPointInfo> filter) {
        return streamClasses(type,filter).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Stream<Class<T>> streamClasses(Class<T> type, Predicate<ExtensionPointInfo> filter) {
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
        extensionPointInfos.stream().filter(options.wrapper(filter)).forEach(updater);
    }
}
