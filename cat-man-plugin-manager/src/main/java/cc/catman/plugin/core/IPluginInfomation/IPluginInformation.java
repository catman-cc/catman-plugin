package cc.catman.plugin.core.IPluginInfomation;

import java.util.List;
import java.util.Map;

public interface IPluginInformation {
    /**
     * 插件名称
     */
    String getName();

    /**
     * 插件分组
     */
    String getGroup();

    /**
     * 插件的版本信息
     */
    String getVersion();

    /**
     * 插件的类型,比如:normal,pom,git等
     */
    String getType();

    Map<String,Object> getSpec();

    /**
     * 获取插件的运行时约束条件
     */
    List<String> getConditions();

    /**
     * 获取插件的依赖信息
     */
    List<IPluginInformation> getDependeices();

    /**
     * 获取插件的插件级依赖
     */
    List<IPluginInformation> getPluginDependeices();
}
