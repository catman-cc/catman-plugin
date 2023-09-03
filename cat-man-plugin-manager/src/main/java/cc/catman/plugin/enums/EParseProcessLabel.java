package cc.catman.plugin.enums;

import cc.catman.plugin.common.Constants;
import cc.catman.plugin.enums.ELabel;

public enum EParseProcessLabel {
    ROUND_CREATE("round-create"),
    ROUND_END("round-end"),

    IN_PROCESS_ID("in-process-id")
    ;
    final private String label;

    EParseProcessLabel(String label) {
        this.label = ELabel.decorate(label);
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
