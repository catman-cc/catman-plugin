package cc.catman.plugin.options;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 插件的参数对象
 */
@Data
public class PluginOption {
    private Set<String> fieldsAllowedToInherit;
    /**
     * 插件运行过程中,运行访问的目录地址
     * 所有的插件在扫描jar时,都不能超出这里允许的范围.
     * 默认值为:System.getProperty("user.dir")
     */
    private List<String> allowedAccessRootDirs;
    /**
     * 插件的自定义类加载策略
     */
    private List<String> globalOrderlyClassLoadingStrategy;

    /**
     * 被禁用的配置类,比如,禁用掉某一个类查找器,或者类型处理器
     */
    private List<String> disabledConfigurationClass;

    /**
     * 将特定的class定义重新指向一个特定的类型定义
     */
    private Map<String,String> redirectToTheSpecifiedClasses;

    /**
     * 为特定的类配置指定的类加载策略
     */
    private Map<String,List<String>> specifiedOrderlyClassLoadingStrategy;

    private List<String> additionalExtensionPoints;

    public boolean validClass(String className){
        return disabledConfigurationClass.stream().noneMatch(c-> c.equals(className));
    }
    public boolean validClass(Class<?> className){
        return validClass(className.getName());

    }
    public boolean validClass(Object obj){
        return validClass(obj.getClass());
    }
}
