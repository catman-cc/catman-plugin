package cc.catman.plugin.operator;

import cc.catman.plugin.extensionPoint.ExtensionPointInfo;

import java.util.function.Predicate;

public class ExtensionPointOperatorHelper {
    public static Predicate<ExtensionPointInfo> allExtensionPointInfo=e->true;

    public static Predicate<ExtensionPointInfo> canUse= ExtensionPointInfo::canUse;

    public static Predicate<ExtensionPointInfo> disabled=ExtensionPointInfo::isDisable;

}
