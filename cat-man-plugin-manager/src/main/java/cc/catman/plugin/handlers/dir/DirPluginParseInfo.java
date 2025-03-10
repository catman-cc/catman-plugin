package cc.catman.plugin.handlers.dir;

import cc.catman.plugin.common.DescribeMapper;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.provider.LocalFileSystemPluginDescribeProvider;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 目录性质的插件
 * 在解析时,将会递归扫描所有指定的目录,并为每个目录生成一个独立的插件,如果有必要的话.
 * 具体的每一个目录,可能会被进一步划分,所以其特征有点类似于{@link LocalFileSystemPluginDescribeProvider}
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class DirPluginParseInfo extends PluginParseInfo {
    static {
        DescribeMapper.getFactory()
                .classMap(PluginParseInfo.class,DirPluginParseInfo.class)
                .field("dynamicValues['dirs']","dirs")
                .field("dynamicValues['supportPluginDescFileNames']","supportPluginDescFileNames")
                .byDefault()
                .register();
    }
    private List<String> dirs;
    private List<String> supportPluginDescFileNames;
}
