package cc.catman.plugin.core.label.metadata;

import cc.catman.plugin.common.Constants;

public enum EMetadata {
    NAME("__$$history$$__"),
    TRANSFER("__$$transfer$$__"),
    ;
    //
    private String label;

    EMetadata(String label) {
        this.label = decorate(label);
    }
    public String label(){
        return this.label;
    }

    public String v(){
        return label();
    }
    public static String decorate(String ...label){
        return Constants.LABEL_PREFIX + String.join("/", label);
    }
}
