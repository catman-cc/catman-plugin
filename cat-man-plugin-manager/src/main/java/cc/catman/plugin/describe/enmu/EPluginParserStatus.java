package cc.catman.plugin.describe.enmu;

/**
 * 插件解析状态
 */
public enum EPluginParserStatus {
    WAIT_PARSE, //等待解析
    COMPLETE, // 解析完成
    FAIL, // 解析失败
}
