package cc.catman.plugin.classloader.handler;

import cc.catman.plugin.runtime.IPluginInstance;

import java.util.*;
import java.util.regex.Pattern;

public class DynamicClassNameClassLoaderHandler implements IClassLoaderHandler {
    private static final String CLASS_FILE_SUFFIX = ".class";

       protected Map<String, Pattern> excludeRegex=new HashMap<>();


    protected List<String> getExcludePackages(IPluginInstance pluginInstance){
        return Optional.ofNullable(pluginInstance.getPluginOptions().getExcludePackages()).orElse(Collections.emptyList());
    }

    protected List<String> getExcludeClasses(IPluginInstance pluginInstance){
        return Optional.ofNullable(pluginInstance.getPluginOptions().getExcludeClasses()).orElse(Collections.emptyList());
    }
    protected List<String> getExcludeRegex(IPluginInstance pluginInstance){
        return Optional.ofNullable(pluginInstance.getPluginOptions().getExcludeRegex()).orElse(Collections.emptyList());
    }

    @Override
    public Payload before(Payload payload) {
        return payload.setContinue(!match(payload.getClassName(), payload.getPluginInstance()));
    }

    public boolean match(String className,IPluginInstance pluginInstance) {
        final String cn = className.endsWith(CLASS_FILE_SUFFIX)
                ? className.substring(0, className.length() - CLASS_FILE_SUFFIX.length())
                : className;

        return getExcludePackages(pluginInstance).stream().anyMatch(cn::startsWith) ||
               getExcludeClasses(pluginInstance).stream().anyMatch(cn::equals) ||
               getExcludeRegex(pluginInstance).stream().anyMatch(r -> excludeRegex.computeIfAbsent(r, Pattern::compile).matcher(cn).matches());
    }
}
