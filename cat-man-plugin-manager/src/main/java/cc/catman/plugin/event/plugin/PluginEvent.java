package cc.catman.plugin.event.plugin;

import cc.catman.plugin.event.AbstractEvent;
import cc.catman.plugin.runtime.EPluginStatus;
import cc.catman.plugin.runtime.IPluginConfiguration;
import cc.catman.plugin.runtime.IPluginInstance;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class PluginEvent extends AbstractEvent {

    private IPluginConfiguration pluginConfiguration;

    private IPluginInstance pluginInstance;

    private EPluginStatus beforeStatus;

}
