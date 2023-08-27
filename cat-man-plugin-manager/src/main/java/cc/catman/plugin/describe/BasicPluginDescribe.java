package cc.catman.plugin.describe;

import cc.catman.plugin.common.GAV;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含了插件所属的最基础信息,也是一个插件所要求的最基本的数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BasicPluginDescribe extends PluginDescribe {
    /**
     * 插件名称
     */
    protected String name;
    /**
     * 插件所属组织
     */
    protected  String group;
    /**
     * 插件的版本信息
     */
    protected String version;

    /**
     * 插件描述文件和插件的相对路径,比如:../../ 表示父级目录的父级目录
     * Note,这里有一个安全问题,就是目录不能跳出项目配置的根目录.
     * 每个插件都必须有该属性
     */
    @Builder.Default
    protected String relativePath="../";

    @Builder.Default
    List<String> extensionsPoints = new ArrayList<>();
    /**
     * 运行时,需要使用的第三方库,这里的第三方库并不是插件,只是普通的jar包
     */
    @Builder.Default
    protected List<GAV> normalDependencyLibraries=new ArrayList<>();
}
