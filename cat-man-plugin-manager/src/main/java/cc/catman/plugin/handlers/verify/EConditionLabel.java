package cc.catman.plugin.handlers.verify;

import cc.catman.plugin.common.Constants;

public enum EConditionLabel {
    TEST(""),
    ;
    private String label;

    EConditionLabel(String label) {
        this.label = decorate(label);
    }

    public String label() {
        return this.label;
    }

    public String v() {
        return label();
    }

    public static String decorate(String... label) {
        return Constants.LABEL_PREFIX + String.join("/", label);
    }
}
