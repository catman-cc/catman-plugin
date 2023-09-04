package cc.catman.plugin.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface Plugin {
    String name() ;
    String group();
    String version()default "0.0.1";
    String source() default "LOCAL";
    String kind() default "JAR";

    String relativePath() default "../";
    String[] libs() default {"libs/"};

    Prop[] properties() default {};
    Gav[] dependencies() default {};
}
