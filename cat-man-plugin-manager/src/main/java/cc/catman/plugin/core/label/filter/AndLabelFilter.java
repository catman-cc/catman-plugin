package cc.catman.plugin.core.label.filter;

import cc.catman.plugin.core.label.ILabelAbility;

import java.util.Collection;
import java.util.List;

public class AndLabelFilter extends GroupLabelFilter{

    public static AndLabelFilter of(ILabelFilter... lf){
        return new AndLabelFilter(lf);
    }

    public static AndLabelFilter of(Collection<ILabelFilter> lf){
        AndLabelFilter andLabelFilter = new AndLabelFilter();
        andLabelFilter.add(lf);
        return andLabelFilter;
    }

    public AndLabelFilter() {
    }

    public AndLabelFilter(ILabelFilter... lf) {
        super(lf);
    }

    public AndLabelFilter(List<ILabelFilter> labelFilters) {
        super(labelFilters);
    }

    @Override
    public boolean filter(ILabelAbility la) {
        return labelFilters.stream().allMatch(lf-> lf.filter(la));
    }
}
