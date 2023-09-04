package cc.catman.plugin.common;

import org.springframework.asm.Type;
import org.springframework.cglib.core.Signature;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ClassHelper {

    public static void doWithSuper(Class<?> clazz, Consumer<Class<?>> consumer){
        Optional.of(clazz.getInterfaces()).ifPresent(is->{
            for (Class<?> i : is) {
                doWithSuper(i,consumer);
            }
        });
        Optional.ofNullable(clazz.getSuperclass()).ifPresent(pc->{
            doWithSuper(pc,consumer);
        });
        consumer.accept(clazz);
    }

    /**
     * 使用鸭嘴模式判断两个类是否可以互相转换
     *
     * @param want   目标类型
     * @param source 现有类型
     * @return true: source具有want的所有公开方法定义
     */
    public static boolean darkLikeStrict(Class<?> want, Class<?> source) {
        // 鸭嘴模式只匹配public方法，其余不管
        List<Signature> sourceMethodSignatures = Stream.of(ReflectionUtils.getAllDeclaredMethods(source))
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .map(m -> {
                ReflectionUtils.makeAccessible(m);
                String name = m.getName();
                Type returnType = Type.getType(m.getReturnType());
                Type[] parametersType = Stream.of(m.getParameterTypes()).map(Type::getType).toArray(Type[]::new);
                return new Signature(name, returnType, parametersType);
            }).collect(Collectors.toList());

        return Stream.of(ReflectionUtils.getAllDeclaredMethods(want))
            .allMatch(m -> {
                ReflectionUtils.makeAccessible(m);
                if (!Modifier.isPublic(m.getModifiers())) {
                    return true;
                }
                String name = m.getName();
                Type returnType = Type.getType(m.getReturnType());
                Type[] parametersType = Stream.of(m.getParameterTypes()).map(Type::getType).toArray(Type[]::new);
                Signature methodSignature = new Signature(name, returnType, parametersType);
                // 名称必须保持一致
                return sourceMethodSignatures.contains(methodSignature);
            });
    }

    /**
     * 使用鸭嘴模式判断两个类是否可以互相转换
     *
     * @param want   目标类型
     * @param source 现有类型
     * @return true: source具有want的所有公开方法定义
     */
    public static boolean darkLike(Class<?> want, Class<?> source) {
        // 鸭嘴模式只匹配public方法，其余不管

        // 简单做一个缓存
        Map<String, Map<Integer, List<MethodDescribe>>> methodsMap = new HashMap<>();

        Stream.of(ReflectionUtils.getAllDeclaredMethods(source))
            .filter(m -> Modifier.isPublic(m.getModifiers()))
            .forEach(m -> {
                String name = m.getName();
                methodsMap
                    .computeIfAbsent(name, (k) -> new HashMap<>())
                    .computeIfAbsent(m.getParameterCount(), (k) -> new ArrayList<>())
                    .add(new MethodDescribe(m));
            });

        return Stream.of(ReflectionUtils.getAllDeclaredMethods(want))
            .allMatch(m -> {
                ReflectionUtils.makeAccessible(m);
                if (!Modifier.isPublic(m.getModifiers())) {
                    return true;
                }
                String name = m.getName();
                if (methodsMap.containsKey(name)) {
                    return false;
                }
                Map<Integer, List<MethodDescribe>> parametersCountMap = methodsMap.get(name);
                if (!parametersCountMap.containsKey(m.getParameterCount())) {
                    return false;
                }
                List<MethodDescribe> candidateMethodDesc = parametersCountMap.get(m.getParameterCount());
                // 想要的方法
                MethodDescribe currentMethodDesc = new MethodDescribe(m);
                return candidateMethodDesc.stream().anyMatch(cmd -> cmd.isAssignableTo(currentMethodDesc));
            });
    }
}
