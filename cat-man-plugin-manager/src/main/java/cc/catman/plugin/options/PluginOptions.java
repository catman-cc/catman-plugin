package cc.catman.plugin.options;

import lombok.Data;
import net.sf.cglib.proxy.Enhancer;

import java.util.*;

/**
 * 为了让代理正确的处理继承关系,所有的属性都不可使用原始类型,已经赋初始值
 * 插件的参数对象,每一个插件实例都拥有属于自己的参数对象,子插件可以继承父插件的配置
 */
@Data
public class PluginOptions {

    private PluginOptions parent;
    public static PluginOptions of(){
        return of(null);
    }

    public static PluginOptions of(PluginOptions parent){
        return new PluginOptions().create(parent);
    }

    protected PluginOptions (){
        // 用于创建空的PluginOptions实例,除此之外,为子类重写留一个扩展
    }
    protected PluginOptions(PluginOptions parent) {
        this.parent = parent;
    }
    public PluginOptions create(PluginOptions parent){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PluginOptions.class);
        enhancer.setCallback(new PluginOptionMethodInterceptor());
        PluginOptions pluginOptions = (PluginOptions) enhancer.create(new Class[]{PluginOptions.class}, new Object[]{parent});
        // 也就意味着,默认情况下,所有的配置都是被继承的
        pluginOptions.setAllInherit(null == parent || parent.isAllInherit());
        return pluginOptions;
    }
    /**
     * 子类继承的参数列表
     */
    private List<String> inherit=new ArrayList<>();

    public boolean contain(String field){
        return inherit.contains(field);
    }
    public PluginOptions createChild(){
        return create(this);
    }

    public PluginOptions createEmpty(){
        return create(null);
    }

    /**
     * 是否允许子类继承自己的全部信息
     */
    private boolean allInherit;


      /**
     * 将特定的class定义重新指向一个特定的类型定义,这是一个很危险的操作,慎用
     */
    private Map<String,String> redirectToTheSpecifiedClasses;

    /**
     * 插件的自定义类加载策略,用户甚至可以在该集合中减少可用的策略
     */
    private List<String> orderlyClassLoadingStrategy;

    /**
     * 为特定的类配置指定的类加载策略,优先级高于{@link #orderlyClassLoadingStrategy}
     */
    private Map<String,List<String>> specifiedOrderlyClassLoadingStrategy;


    /**
     * 为插件,额外设置扩展点信息
     */
    private List<String> additionalExtensionPoints;

    /**
     * 根据一定规则,禁止插件访问指定的类
     */
    private List<String> excludeClasses;

    private List<String> excludePackages;

    private List<String> excludeRegex;

    /**
     * 插件运行过程中,运行访问的目录地址
     * 所有的插件在扫描jar时,都不能超出这里允许的范围.
     * 默认值为:System.getProperty("user.dir")
     */
//    private List<String> allowedAccessRootDirs;

}
