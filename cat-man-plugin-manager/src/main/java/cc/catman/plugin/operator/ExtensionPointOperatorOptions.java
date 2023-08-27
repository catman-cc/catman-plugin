package cc.catman.plugin.operator;

import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import lombok.Builder;
import lombok.Data;

import java.util.function.Predicate;

@Data
@Builder
public class ExtensionPointOperatorOptions {
    /**
     * 仅包含处于Ready状态的插件
     */
    private boolean onlyCanUse;

    private boolean onlyDisabled;

    public Predicate<ExtensionPointInfo> wrapper(Predicate<ExtensionPointInfo> filter){
        if (onlyCanUse){
            return ExtensionPointOperatorHelper.canUse.and(filter);
        }
        if (onlyDisabled){
            return ExtensionPointOperatorHelper.disabled.and(filter);
        }
        return filter;
    }
    public Predicate<ExtensionPointInfo> wrapper(){
        if (onlyCanUse){
            return ExtensionPointOperatorHelper.canUse;
        }
        if (onlyDisabled){
            return ExtensionPointOperatorHelper.disabled;
        }
        return ExtensionPointOperatorHelper.allExtensionPointInfo;
    }
}
