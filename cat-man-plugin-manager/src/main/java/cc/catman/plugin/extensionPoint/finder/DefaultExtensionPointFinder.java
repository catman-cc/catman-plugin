package cc.catman.plugin.extensionPoint.finder;

import cc.catman.plugin.classloader.exceptions.ClassLoadRuntimeException;
import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEvent;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEventName;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import cc.catman.plugin.runtime.IPluginInstance;

import java.util.Objects;
import java.util.stream.Stream;

public class DefaultExtensionPointFinder implements IExtensionPointFinder {
    private IPluginInstance pluginInstance;

    public DefaultExtensionPointFinder(IPluginInstance pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public Stream<ExtensionPointInfo> find(StandardPluginDescribe standardPluginDescribe) {
        return standardPluginDescribe.getExtensionsPoints().stream().map(name -> {
            try {
                Class<?> clazz = pluginInstance.getClassLoader().loadClass(name);
                return ExtensionPointInfo.builder()
                        .className(name)
                        .clazz(clazz)
                        .valid(true)
                        .build();
            } catch (ClassNotFoundException | ClassLoadRuntimeException e) {
                pluginInstance.getPluginManager().getPluginConfiguration().publish(ExtensionPointEvent.of(ExtensionPointEventName.NOT_FOUND.name(), pluginInstance.getExtensionPointManager())
                        .setError(e)
                        .setStandardPluginDescribe(standardPluginDescribe));
            }
            return null;
        }).filter(Objects::nonNull);

    }
}
