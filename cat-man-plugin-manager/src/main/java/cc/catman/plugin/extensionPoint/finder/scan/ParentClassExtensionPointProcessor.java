package cc.catman.plugin.extensionPoint.finder.scan;

import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import org.springframework.util.ClassUtils;

import java.util.List;

public class ParentClassExtensionPointProcessor implements IExtensionPointProcessor{
    List<Class<?>> parentClasses;

    public ParentClassExtensionPointProcessor(List<Class<?>> parentClasses) {
        this.parentClasses = parentClasses;
    }

    @Override
    public void handle(ExtensionPointInfo extensionPointInfo) {
        this.parentClasses.forEach(pc->{
            if (ClassUtils.isAssignable(pc, extensionPointInfo.getClazz())){
                extensionPointInfo.setValid(true);
            }
        });
    }
}
