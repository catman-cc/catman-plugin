package cc.catman.plugin.event.parser;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.event.AbstractEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PluginParseEvent extends AbstractEvent {
    /**
     * 插件解析信息
     */
    private PluginParseInfo parseInfo;
    /**
     * 插件解析后的信息
     */
    private List<PluginParseInfo> parsedList;
}
