package cc.catman.plugin.extensionPoint.finder;

import cc.catman.plugin.classloader.exceptions.ClassLoadRuntimeException;
import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEvent;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEventName;
import cc.catman.plugin.event.extensionPoint.ExtensionPointInfoEvent;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import cc.catman.plugin.runtime.IPluginInstance;

import java.util.Optional;
import java.util.stream.Stream;

public class AdditionalExtensionPointFinder implements IExtensionPointFinder{
    private IPluginInstance pluginInstance;

    public AdditionalExtensionPointFinder(IPluginInstance pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public Stream<ExtensionPointInfo> find(StandardPluginDescribe standardPluginDescribe) {
        return Optional.ofNullable(this.pluginInstance.getPluginOptions().getAdditionalExtensionPoints())
                .map(eps-> eps.stream().map(name->{
                      try {
                          Class<?> clazz = pluginInstance.getClassLoader().loadClass(name);
                          return ExtensionPointInfo.builder()
                                  .className(name)
                                  .clazz(clazz)
                                  .valid(true)
                                  .build();
                      } catch (ClassNotFoundException | ClassLoadRuntimeException e) {
                          pluginInstance.getPluginManager().getPluginConfiguration().publish(ExtensionPointInfoEvent.of(ExtensionPointEventName.NOT_FOUND.name(), pluginInstance.getExtensionPointManager())
                                  .setError(e)
                                  .setStandardPluginDescribe(standardPluginDescribe));
                      }
                      return null;
                  })).orElseGet(Stream::empty);
    }
}
