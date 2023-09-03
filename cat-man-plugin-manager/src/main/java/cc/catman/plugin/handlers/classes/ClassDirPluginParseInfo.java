package cc.catman.plugin.handlers.classes;

import cc.catman.plugin.common.DescribeMapper;
import cc.catman.plugin.core.describe.PluginParseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ClassDirPluginParseInfo extends PluginParseInfo {
    static {
        DescribeMapper.getFactory()
                .classMap(PluginParseInfo.class, ClassDirPluginParseInfo.class)
                .field("dynamicValues['dirs']","dirs")
                .field("dynamicValues['libAntPatterns']","libAntPatterns")
                .byDefault()
                .register();
    }
    /**
     *  class文件存放的目录,会配合sourceDirs生成一个完整的地址,比如base的值是~/project,其中一个dirs的值./sub-project/target/classes,
     *  那么~/project/sub-project/target/classes下的类将会被加载.
     *
     *  dirs设计成数组的原因是因为,如果是一个多模块的项目,在开发阶段,可能并不想配置那么多的插件描述文件.
     *  其实配合cat-man-plugin-annotation-processor插件,可以自动生成对应的插件描述文件.
     *
     *  想了想,这里还是需要做一些调整,应该额外的加一些参数,在尝试配置某个目录是否做为插件的时候,应该优先检查目录下是否有插件描述文件,如果有的话,
     *  那个目录应该被设置为单独的插件.
     *
     */
    /**
     * 如果一个插件对应多个源码目录,这个配置会很有用
     */
    protected List<String> dirs=new ArrayList<>();
    /**
     * 插件使用的扩展lib地址,可以有多个,理论上地址的必须是该jar插件的子目录
     */
    protected List<String> libAntPatterns=new ArrayList<>();
}
