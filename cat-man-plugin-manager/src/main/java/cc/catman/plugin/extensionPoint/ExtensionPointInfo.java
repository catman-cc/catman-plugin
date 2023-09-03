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
    @Builder.Default
    private Map<Class<?>,Class<?>> overloads=new HashMap<>();

    public boolean canUse(){
        return isValid()&&!isDisable();
    }
    public Class<?> toClass(Class<?> want){
        return overloads.getOrDefault(want,getClazz());
    }

    public ExtensionPointInfo deepNew(){
       return ExtensionPointInfo.builder()
                .className(getClassName())
                .clazz(getClazz())
                .disable(isDisable())
                .valid(isValid())
                .object(getObject())
                .overloads(new HashMap<>(getOverloads()))
                .build();

    }
}
