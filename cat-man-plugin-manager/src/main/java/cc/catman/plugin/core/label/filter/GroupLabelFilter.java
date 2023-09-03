package cc.catman.plugin.core.label.filter;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 成组的标签处理器,可以通过组合多个标签处理器,构成复杂的关联关系
 */
public abstract  class GroupLabelFilter implements ILabelFilter{
    @Getter
    protected List<ILabelFilter> labelFilters;

    public GroupLabelFilter() {

    }
    public GroupLabelFilter(ILabelFilter... labelFilters) {
        this();
        add(labelFilters);
    }

    public GroupLabelFilter(Collection<ILabelFilter> labelFilters) {
        this();
        add(labelFilters);
    }

    public GroupLabelFilter add(ILabelFilter l){
        this.labelFilters.add(l);
        return this;
    }
    public GroupLabelFilter add(ILabelFilter... ls){
        this.labelFilters.addAll(Arrays.asList(ls));
        return this;
    }

    public GroupLabelFilter add(Collection<ILabelFilter> ls){
        this.labelFilters.addAll(ls);
        return this;
    }
}
