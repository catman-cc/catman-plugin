package cc.catman.plugin.describe.handler.mvn;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.describe.PluginParseInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MvnCommandPluginParseInfo extends PluginParseInfo {
    /**
     * 插件所属组织
     */
    protected  String group;
    /**
     * 插件名称
     */
    protected String name;

    /**
     * 插件的版本信息
     */
    protected String version;

}
