package cc.catman.plugin.enums;

import cc.catman.plugin.common.Constants;

public enum ELabel {
    DEFAULT_HISTORY("__$$history$$__"),
    PERSISTED("persisted"), // 长期存在的,此处的标签不应该被随意移除
    INSTANTANEOUS("instantaneous"), // 瞬时的标签,该标签可以在合适的时机移除
    ;
    private String label;

    ELabel(String label) {
        this.label = decorate(label);
    }
    public String label(){
        return this.label;
    }

    public String v(){
        return label();
    }
    public String derive(String ...labels){
        return label+"/" + String.join("/", labels);
    }
    public static String decorate(String ...label){
        return Constants.LABEL_PREFIX + String.join("/", label);
    }
}
