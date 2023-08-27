package cc.catman.plugin.operator;

import cc.catman.plugin.runtime.IPluginInstance;
import lombok.Builder;
import lombok.Data;

import java.util.function.Predicate;

@Data
@Builder
public class PluginOperatorOptions {
    /**
     * 仅包含处于Ready状态的插件
     */
    private boolean onlyReady;

    public Predicate<IPluginInstance> wrapper(Predicate<IPluginInstance> pluginFilter){
        if (onlyReady){
            return PluginOperatorHelper.ready.and(pluginFilter);
        }
        return pluginFilter;
    }
}
