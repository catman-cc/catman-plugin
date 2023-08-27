package cc.catman.plugin.operator;


import cc.catman.plugin.extensionPoint.ExtensionPointInfo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 扩展点访问接口定义,定义一系列加载扩展点的行为
 */
public interface IExtensionPointOperator {

     List<Class<?>> list();
    Stream<Class<?>> stream();

    /**
     * 获取所有指定类型的扩展实例
     *
     * @param type 类型
     * @param <T>  扩展点类型定义
     * @return 所有实例
     */
    default <T> List<T> list(Class<T> type){
        return list(type, ExtensionPointInfo::canUse, e->true);
    }

    <T> Optional<T> first(Class<T> type);

    <T> List<T> list(Class<T> type, Predicate<ExtensionPointInfo> beforeInstance,Predicate<T> afterInstance);

    default <T> List<T> listWithInstanceFactory(Class<T> type, Function<Class<T>,T > factory){
        return listWithInstanceFactory(type,factory, ExtensionPointInfo::canUse, e->true);
    }
    <T> List<T> listWithInstanceFactory(Class<T> type, Function<Class<T>,T > factory, Predicate<ExtensionPointInfo> beforeInstance,Predicate<T> afterInstance);

   default  <T> List<Class<T>> listClazz(Class<T> type){
       return listClazz(type,e->true);
   }

    <T> List<Class<T>> listClazz(Class<T> type, Predicate<ExtensionPointInfo> filter);

    /**
     * 获取所有指定类型的扩展实例 --- Stream方式
     *
     * @param type 类型
     * @param <T>  扩展点类型定义
     * @return 所有实例
     */
   default  <T> Stream<T> stream(Class<T> type){
       return stream(type,e->true);
   }

    <T> Stream<T> stream(Class<T> type, Predicate<ExtensionPointInfo> filter);

    default <T> Stream<T> streamWithInstanceFactory(Class<T> type, Function<Class<T>,T > factory){
        return streamWithInstanceFactory(type,factory,e->true);
    }
    <T> Stream<T> streamWithInstanceFactory(Class<T> type, Function<Class<T>,T > factory, Predicate<ExtensionPointInfo> filter);

    default  <T> Stream<Class<T>> streamClasses(Class<T> type){
        return streamClasses(type,e->true);
    }

    <T> Stream<Class<T>> streamClasses(Class<T> type, Predicate<ExtensionPointInfo> filter);


    <T> Optional<Class<T>> firstClass(Class<T> type);

    default boolean disable(Predicate<ExtensionPointInfo> filter){
        AtomicBoolean res=new AtomicBoolean(false);
        update(filter,(extensionPointInfo -> {
            res.set(!extensionPointInfo.isDisable());
            extensionPointInfo.setDisable(true);
        }));
        return res.get();
    }

    default boolean enable(Predicate<ExtensionPointInfo> filter){
        AtomicBoolean res=new AtomicBoolean(false);
        update(filter,(extensionPointInfo -> {
            res.set(extensionPointInfo.isDisable());
            extensionPointInfo.setDisable(false);
        }));
        return res.get();
    }

    void update(Predicate<ExtensionPointInfo> filter, Consumer<ExtensionPointInfo> updater);
}
