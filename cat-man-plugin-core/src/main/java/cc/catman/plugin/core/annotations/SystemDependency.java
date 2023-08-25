package cc.catman.plugin.core.annotations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SystemDependency {
    Plugin[] value();
}
