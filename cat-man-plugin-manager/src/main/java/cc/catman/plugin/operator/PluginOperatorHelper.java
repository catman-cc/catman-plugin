package cc.catman.plugin.operator;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.runtime.EPluginStatus;
import cc.catman.plugin.runtime.IPluginInstance;

import java.util.function.Predicate;

public class PluginOperatorHelper {
    public static final int MAX_DEEP=Integer.MAX_VALUE;
    /**
     * 匹配所有
     */
   public static  Predicate<IPluginInstance> all=p->true;

    /**
     * 获取所有可用的插件
     */
    public static  Predicate<IPluginInstance> ready = p-> EPluginStatus.READY.equals(p.getStatus());

    /**
     * 获取所有被禁用的插件
     */
    public static  Predicate<IPluginInstance> disabled=p-> EPluginStatus.DISABLED.equals(p.getStatus());

    public static  Predicate<IPluginInstance> createGavFilter(GAV gav){
        return pluginInstance -> GAV.builder().group(pluginInstance.getGroup()).name(pluginInstance.getName()).version(pluginInstance.getVersion()).build()
                  .Match(gav);
    }

}
