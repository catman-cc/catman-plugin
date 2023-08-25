package cc.catman.plugin.event.extensionPoint;

import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ExtensionPointInfoEvent extends ExtensionPointEvent{
    private ExtensionPointInfo extensionPointInfo;

    public static ExtensionPointInfoEvent of(String eventName, IExtensionPointManager extensionPointManager,ExtensionPointInfo extensionPointInfo){
        return ExtensionPointInfoEvent.builder()
                .extensionPointInfo(extensionPointInfo)
                .extensionPointManager(extensionPointManager)
                .pluginConfiguration(extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration())
                .pluginInstance(extensionPointManager.getPluginInstance())
                .eventName(eventName)
                .build();
    }
}
