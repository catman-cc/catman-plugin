package cc.catman.plugin.event.extensionPoint;

import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.event.AbstractEvent;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.extensionPoint.IExtensionPointMatcher;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginInstance;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ExtensionPointEvent extends AbstractEvent {

    private IPluginConfiguration pluginConfiguration;

    private IPluginInstance pluginInstance;

    private IExtensionPointManager extensionPointManager;

    private PluginDescribe pluginDescribe;

    private Throwable error;


    public static ExtensionPointEvent of(String eventName, IExtensionPointManager extensionPointManager){
        return ExtensionPointEvent.builder()
                .extensionPointManager(extensionPointManager)
                .pluginConfiguration(extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration())
                .pluginInstance(extensionPointManager.getPluginInstance())
                .eventName(eventName)
                .build();
    }
}
