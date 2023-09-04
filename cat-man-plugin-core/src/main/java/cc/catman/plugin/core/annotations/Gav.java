package cc.catman.plugin.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Gav {
    String name()  ;
    String group() default "";
    String version() default "";
}
