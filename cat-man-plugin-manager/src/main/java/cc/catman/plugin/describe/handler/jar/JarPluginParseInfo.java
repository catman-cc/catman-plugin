package cc.catman.plugin.describe.handler.jar;

import cc.catman.plugin.describe.PluginParseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JarPluginParseInfo extends PluginParseInfo {

    /**
     * jar类型插件使用的扩展lib地址,可以有多个,理论上地址的必须是该jar插件的子目录
     */
    protected List<String> libAntPatterns=new ArrayList<>();

}
