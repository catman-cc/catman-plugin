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
    private ExtensionPointInfo beforeChange;
    private ExtensionPointInfo extensionPointInfo;
    private Object obj;
    public static ExtensionPointInfoEvent of(String eventName, IExtensionPointManager extensionPointManager){
        return ExtensionPointInfoEvent.builder()
                .extensionPointManager(extensionPointManager)
                .pluginConfiguration(extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration())
                .pluginInstance(extensionPointManager.getPluginInstance())
                .standardPluginDescribe(extensionPointManager.getPluginInstance().getPluginParseInfo())
                .eventName(eventName)
                .build();
    }
    public static ExtensionPointInfoEvent of(String eventName, IExtensionPointManager extensionPointManager,ExtensionPointInfo extensionPointInfo){
        return ExtensionPointInfoEvent.builder()
                .extensionPointInfo(extensionPointInfo)
                .extensionPointManager(extensionPointManager)
                .pluginConfiguration(extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration())
                .pluginInstance(extensionPointManager.getPluginInstance())
                .standardPluginDescribe(extensionPointManager.getPluginInstance().getPluginParseInfo())
                .eventName(eventName)
                .build();
    }
    public static ExtensionPointInfoEvent of(String eventName, IExtensionPointManager extensionPointManager,ExtensionPointInfo extensionPointInfo,Object obj){
        return ExtensionPointInfoEvent.builder()
                .extensionPointInfo(extensionPointInfo)
                .extensionPointManager(extensionPointManager)
                .standardPluginDescribe(extensionPointManager.getPluginInstance().getPluginParseInfo())
                .obj(obj)
                .pluginConfiguration(extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration())
                .pluginInstance(extensionPointManager.getPluginInstance())
                .eventName(eventName)
                .build();
    }
    public static ExtensionPointInfoEvent of(String eventName, IExtensionPointManager extensionPointManager,ExtensionPointInfo old,ExtensionPointInfo extensionPointInfo){
        return ExtensionPointInfoEvent.builder()
                .beforeChange(old)
                .standardPluginDescribe(extensionPointManager.getPluginInstance().getPluginParseInfo())
                .extensionPointInfo(extensionPointInfo)
                .extensionPointManager(extensionPointManager)
                .pluginConfiguration(extensionPointManager.getPluginInstance().getPluginManager().getPluginConfiguration())
                .pluginInstance(extensionPointManager.getPluginInstance())
                .eventName(eventName)
                .build();
    }
}
