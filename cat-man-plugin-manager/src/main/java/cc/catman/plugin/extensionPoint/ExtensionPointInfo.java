package cc.catman.plugin.extensionPoint;

import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * 扩展点描述信息
 */
@Data
@Builder
public class ExtensionPointInfo {
    /**
     * 扩展点类名
     */
    private String className;

    /**
     * 类定义
     */
    private Class<?> clazz;

    /**
     * 可以转换为的类型定义,一个类可能对应着多个扩展点的实现
     */
    private Set<Class<?>> supportTypes=new HashSet<>();

    /**
     * 该类是否被禁用
     */
    private boolean disable=false;

    private boolean valid=false;

    /**
     * 实例化的对象,只有全局单例的时候,该对象才会有值
     */
    private Object object;

    /**
     * 允许针对特定类型重写类定义
     */
    private Map<Class<?>,Class<?>> overloads=new HashMap<>();
    public void addSupportType(Class<?> clazz){
        this.supportTypes.add(clazz);
        this.valid=true;
    }
    public Class<?> toClass(Class<?> want){
        return overloads.getOrDefault(want,getClazz());
    }

    public void merge(ExtensionPointInfo other){
        if (other.clazz!=this.clazz){
            addSupportType(other.clazz);
        }
        supportTypes.addAll(other.supportTypes);
        overloads.putAll(other.overloads);
    }
}
