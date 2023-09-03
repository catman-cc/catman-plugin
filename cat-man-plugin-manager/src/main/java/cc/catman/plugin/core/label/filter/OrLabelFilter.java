package cc.catman.plugin.core.label.filter;

import cc.catman.plugin.core.label.ILabelAbility;

public class OrLabelFilter extends GroupLabelFilter {
    @Override
    public boolean filter(ILabelAbility la) {
        return labelFilters.stream().anyMatch(lf->lf.filter(la));
    }
}
