package cc.catman.plugin.extensionPoint.finder.scan;

import lombok.Data;

import java.lang.reflect.Modifier;
import java.util.Optional;
@Data
public class ExtensionPointMatchOptions {

    /**
     * 跳过抽象类
     */
    private Optional<Boolean> skipAbstract = Optional.empty();

    /**
     * 跳过接口类型
     */
    private Optional<Boolean> skipInterface = Optional.empty();

    /**
     * 限制必须是公开类
     */
    private Optional<Boolean> requirePublic = Optional.empty();

    public boolean isSkip(Class<?> clazz) {

        int modifier = clazz.getModifiers();
        if (getSkipAbstract().orElse(false) && Modifier.isAbstract(modifier)) {
            return true;
        }
        return getSkipInterface().orElse(false)
                && Modifier.isInterface(modifier)
                && requirePublic.orElse(false) && Modifier.isPublic(modifier)
                ;
    }
}
