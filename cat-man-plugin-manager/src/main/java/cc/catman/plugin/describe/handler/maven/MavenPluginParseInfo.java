package cc.catman.plugin.describe.handler.maven;

import cc.catman.plugin.describe.PluginParseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.nio.file.Path;

/**
 * Maven类型的插件描述
 *
 * 最开始考虑每一个maven插件都可以根据自己的需要来控制访问仓库地址,授权信息等内容.
 *
 * 但是后来认真考虑后,这种操作应该交给专业的软件去完成,比如nexus.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MavenPluginParseInfo extends PluginParseInfo {
    private Path pluginDir;
}
