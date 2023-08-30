package cc.catman.plugin.describe.enmu;

import cc.catman.plugin.common.Constants;

public enum ELabel {
    PERSISTED("persisted"), // 长期存在的,此处的标签不应该被随意移除
    INSTANTANEOUS("instantaneous"), // 瞬时的标签,该标签可以在合适的时机移除
    ;

    private String label;
    private String label(){
        return this.label;
    }
     ELabel(String label) {
        this.label = decorate(label);
    }

    public String derive(String ...labels){
        return label+String.join(label,"/");
    }
    public static String decorate(String ...label){
        return Constants.LABEL_PREFIX + String.join("/", label);
    }
}
