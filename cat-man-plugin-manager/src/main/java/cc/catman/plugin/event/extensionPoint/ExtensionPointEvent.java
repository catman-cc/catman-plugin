package cc.catman.plugin.event.extensionPoint;

import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.event.AbstractEvent;
import cc.catman.plugin.extensionPoint.IExtensionPointManager;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginInstance;
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

    private StandardPluginDescribe standardPluginDescribe;

    private Throwable error;



}
