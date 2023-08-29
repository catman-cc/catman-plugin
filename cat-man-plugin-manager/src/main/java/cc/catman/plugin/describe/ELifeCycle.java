package cc.catman.plugin.describe;

/**
 * 插件在解析过程中,根据KIND和
 * 插件在不同的生命周期,包含不同的数据
 */
public enum ELifeCycle {
    validate,  // 验证插件是否可用
    initialize, // 初始化插件
    process_sources,
    compile,

}
