package cc.catman.plugin.core.label;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface L {
    String name();

    String[] value() default {};

    ELabelType type() default ELabelType.ADD;
}
