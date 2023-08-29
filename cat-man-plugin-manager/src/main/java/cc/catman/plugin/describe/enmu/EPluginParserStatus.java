package cc.catman.plugin.describe.enmu;

/**
 * 插件解析状态
 */
public enum EPluginParserStatus {
    RE_PARSE, // 需要重新进行解析操作
    WAIT_PARSE, //等待解析
    COMPLETE, // 解析完成
    FAIL, // 解析失败
}
