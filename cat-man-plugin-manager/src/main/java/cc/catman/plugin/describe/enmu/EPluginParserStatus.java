package cc.catman.plugin.describe.enmu;

/**
 * 插件解析状态
 */
public enum EPluginParserStatus {
    WAIT_PARSE, //等待解析
    PARSED, // 初次解析完成
    NEED_OTHER_PROCESS, // 虽然经过了解析,但是需要其他解析处理器,进一步解析

    SUCCESS, // 解析完成
    FAIL, // 解析失败

}
