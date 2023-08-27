package cc.catman.plugin.operator;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.runtime.IPluginInstance;

import java.util.List;
import java.util.function.Predicate;

/**
 * 通过插件访问扩展点
 */
public interface IPluginExtensionPointOperator {

    default <T> List<T> list(Class<T> type){
        return list(type, PluginOperatorHelper.all, PluginOperatorHelper.MAX_DEEP);
    }
   default  <T> List<T> list(Class<T> type , GAV gav){
        return list(type,gav, PluginOperatorHelper.MAX_DEEP);
   }

    // 限制类型 ,限制插件,限制插件的层级,是否查找插件的依赖项,以及依赖的深度
    <T> List<T> list(Class<T> type, GAV gav,int deep);

    // 限制类型 ,插件过滤器,限制插件的层级,是否查找插件的依赖项,以及依赖的深度
    <T> List<T> list(Class<T> type, Predicate<IPluginInstance> pluginFilter, int deep);
}
