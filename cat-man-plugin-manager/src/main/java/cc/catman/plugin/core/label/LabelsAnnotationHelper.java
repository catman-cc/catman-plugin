package cc.catman.plugin.core.label;

import cc.catman.plugin.common.ClassHelper;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Optional;

public class LabelsAnnotationHelper {
    public static <T extends ILabelAbility> T handler(T la) {
        Labels labels = la.getLabels();
        // 子覆盖父,默认合并
        ClassHelper.doWithSuper(la.getClass(), (c) -> {
            Optional.ofNullable(AnnotationUtils.findAnnotation(la.getClass(), Ls.class)).ifPresent(ls -> {
                for (L l : ls.value()) {
                    handlerWithType(labels, l);
                }
            });
            Optional.ofNullable(AnnotationUtils.findAnnotation(la.getClass(), L.class)).ifPresent(l -> {
                handlerWithType(labels, l);
            });
        });
        return la;
    }

    public static void handlerWithType(Labels labels, L l) {
        switch (l.type()) {
            case ADD: {
                labels.add(l.name(), l.value());
                break;
            }
            case REMOVE: {
                labels.rm(l.name(), l.value());
                break;
            }
            case REPLACE: {
                labels.replace(l.name(), l.value());
                break;
            }
        }
    }
}
