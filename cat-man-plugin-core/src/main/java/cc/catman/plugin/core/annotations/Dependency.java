package cc.catman.plugin.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 插件的依赖描述配置,此处是插件依赖,不是普通的lib仓库依赖
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {
    Plugin[] value();
}
