package cc.catman.plugin.operator;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.runtime.PluginInstanceTree;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IPluginOperator {
    IPluginExtensionPointOperator createPluginExtensionPointVisitor();

    default void every(Consumer<IPluginInstance> visitor) {
        every(PluginOperatorHelper.all, PluginOperatorHelper.MAX_DEEP, visitor);
    }

    default void every(int deep, Consumer<IPluginInstance> visitor) {
        every(PluginOperatorHelper.all, deep, visitor);
    }

    default void every(Predicate<IPluginInstance> pluginFilter,  Consumer<IPluginInstance> visitor){
        every(pluginFilter, PluginOperatorHelper.MAX_DEEP,visitor);
    }
    void every(Predicate<IPluginInstance> pluginFilter, int deep, Consumer<IPluginInstance> visitor);

    PluginInstanceTree tree();

    PluginInstanceTree tree(GAV gav, int deep);

    PluginInstanceTree tree(Predicate<IPluginInstance> pluginFilter, int deep);

    default List<IPluginInstance> all() {
        return list(PluginOperatorHelper.all, PluginOperatorHelper.MAX_DEEP);
    }

    // 限制类型 ,限制插件,限制插件的层级,是否查找插件的依赖项,以及依赖的深度
    List<IPluginInstance> list(GAV gav, int deep);

    // 限制类型 ,插件过滤器,限制插件的层级,是否查找插件的依赖项,以及依赖的深度
    List<IPluginInstance> list(Predicate<IPluginInstance> pluginFilter, int deep);

}
