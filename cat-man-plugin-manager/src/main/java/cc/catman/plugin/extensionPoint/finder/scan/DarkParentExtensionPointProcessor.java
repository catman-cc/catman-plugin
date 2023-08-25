package cc.catman.plugin.extensionPoint.finder.scan;

import cc.catman.plugin.common.ClassHelper;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;

import java.util.List;

/**
 * 尝试通过asm动态创建指定类型转换后的定义,发现比较麻烦,那就不如不做了,先留着,如果后续有更好的解决方案,再完善
 *
 */
@Deprecated
public class DarkParentExtensionPointProcessor implements IExtensionPointProcessor{
    List<Class<?>> parentClasses;
    protected boolean useStrict = false;

    @Override
    public void handle(ExtensionPointInfo extensionPointInfo) {
        Class<?> clazz=extensionPointInfo.getClazz();
        this.parentClasses.forEach(p->{
            if (useStrict
                    ? ClassHelper.darkLikeStrict(p, clazz)
                    : ClassHelper.darkLike(p, clazz)){
                // 直接注册没有用,还需要生成代理类
                extensionPointInfo.addSupportType(p);
            }
        });
    }
}
