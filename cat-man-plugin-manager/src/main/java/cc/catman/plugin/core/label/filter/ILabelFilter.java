package cc.catman.plugin.core.label.filter;

import cc.catman.plugin.core.label.ILabelAbility;

/**
 * 用于过滤标签的接口
 */
@FunctionalInterface
public interface ILabelFilter {
    ILabelFilter TRUE= l -> true;
    ILabelFilter FALSE= l -> false;
    /**
     *  过滤标签,并返回
     * @param l 被标签化的实体
     * @return 是否满足要求
     */
    boolean filter(ILabelAbility l);
}
