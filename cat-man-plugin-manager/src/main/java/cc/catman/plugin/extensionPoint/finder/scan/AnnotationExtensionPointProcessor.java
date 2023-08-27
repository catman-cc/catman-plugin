package cc.catman.plugin.extensionPoint.finder.scan;

import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import lombok.Setter;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AnnotationExtensionPointProcessor implements IExtensionPointProcessor{
    private List<Class<? extends Annotation>> annotations;

    @Setter
    private boolean addParentClass=true;
    @Setter
    private boolean addAllInterface=true;

    public AnnotationExtensionPointProcessor(List<Class<? extends Annotation>> annotations) {
        this.annotations = annotations;
    }

    public AnnotationExtensionPointProcessor(List<Class<? extends Annotation>> annotations, boolean addParentClass, boolean addAllInterface) {
        this.annotations = annotations;
        this.addParentClass = addParentClass;
        this.addAllInterface = addAllInterface;
    }

    @Override
    public void handle(ExtensionPointInfo extensionPointInfo) {
        Class<?> clazz=extensionPointInfo.getClazz();
        if (AnnotationUtils.isCandidateClass(clazz, annotations)){
            extensionPointInfo.setValid(true);
        }
    }
}
