package cc.catman.plugin.enums;

/**
 * 合并策略,提供合并策略的原因在于,等后面出现插件市场控制台的时候,我们可以通过控制台有效的控制插件的运行行为.
 *
 */
public enum EMergeStrategy {
    CURRENT_FIRST, // 当前配置优先
    TARGET_FIRST,// TARGET优先

    MERGE_CURRENT_FIRST,// 对于对象和集合进行合并操作,合并时如果出现同名数据,当前配置优先
    MERGE_TARGET_FIST, // 对于对象和集合进行合并操作,合并时如果出现同名数据,目标配置优先
}
