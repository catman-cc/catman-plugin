package cc.catman.plugin.describe.enmu;

/**
 * 插件描述标签
 */
public enum EDescribeLabel {
    EXCLUSIVE_PARSER("exclusive-parser"), // 定义一个专属解析器,在解析时,如果存在改标签,解析器必须持有符合的标签才可以进行解析操作.
    DESCRIBE_FILE_PARSED("describe-file-parsed") ,// 描述文件已经被解析完成
    NORMAL_LIBS_ADDED("normal-libs-added"),// 普通的依赖已被添加
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
        return label+String.join(label,"/");
    }
    public static String decorate(String ...label){
        return ELabel.INSTANTANEOUS.derive(label);
    }
}
