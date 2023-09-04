package cc.catman.plugin.enums;


import cc.catman.plugin.enums.ELabel;

/**
 * 插件描述标签
 */
public enum EDescribeLabel {
    EXCLUSIVE_PARSER("exclusive-parser"), // 定义一个专属解析器,在解析时,如果存在改标签,解析器必须持有符合的标签才可以进行解析操作.
    DESCRIBE_INFO_FILLED("describe-info-filled") ,// 插件的描述信息已经被填充完毕
    NORMAL_LIBS_ADDED("normal-libs-added"),// 普通的依赖已被添加

    NEED_DOWNLOAD_FROM_MARKET_PLACE("need-download-from-market-place"),// 需要从插件市场下载

    PARSE_HANDLER_CHAIN("parse-handler-chain"), // 解析链

    LOCAL_CACHED("local_cached"),
    LOAD_FROM_CACHE("local_from_cache") ,
    ;
    private String label;

    EDescribeLabel(String label) {
        this.label = decorate(label);
    }
    public String label(){
        return this.label;
    }
    // 由改标签衍生出来的子标签

    public String derive(String ...labels){
        return label + String.join("/", labels);
    }
    public static String decorate(String ...label){
        return ELabel.INSTANTANEOUS.derive(label);
    }
}
