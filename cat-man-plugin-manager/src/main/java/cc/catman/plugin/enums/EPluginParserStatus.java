package cc.catman.plugin.enums;

/**
 * 插件解析状态
 */
public enum EPluginParserStatus {
    RE_PARSE, // 需要重新进行解析操作
    PROCESSING, //等待解析
    COMPLETE, // 解析完成
    FAIL, // 解析失败

    SUPERSEDED, // 被取代
}
