package cc.catman.plugin.core.label;

/**
 * 标签能力
 */
public interface ILabelAbility {
    default Labels labels(){
        return getLabels();
    }

     Labels getLabels();
}
