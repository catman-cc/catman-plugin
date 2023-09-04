package cc.catman.plugin.core.label.filter;

public class LabelFilterBuilder {

    private final GroupLabelFilter filter;

    public static LabelFilterBuilder create(){
        return new LabelFilterBuilder();
    }
    public LabelFilterBuilder() {
        this(new AndLabelFilter());
    }

    public LabelFilterBuilder(GroupLabelFilter filter) {
        this.filter = filter;
    }

    public AndLabelFilterBuilder and(){
        AndLabelFilter andLabelFilter=new AndLabelFilter();
        filter.add(andLabelFilter);
        return new AndLabelFilterBuilder(this,andLabelFilter);
    }

    public OrLabelFilterBuild or(){
        OrLabelFilter orLabelFilter=new OrLabelFilter();
        filter.add(orLabelFilter);
        return new OrLabelFilterBuild(this,orLabelFilter);

    }

    public ILabelFilter build(){
        return this.filter;
    }

    public static class AndLabelFilterBuilder{
        private final LabelFilterBuilder s;
        private final AndLabelFilter andLabelFilter;

        public AndLabelFilterBuilder(LabelFilterBuilder s, AndLabelFilter andLabelFilter) {
            this.s = s;
            this.andLabelFilter = andLabelFilter;
        }

        public LabelFilterBuilder end(){
            return s;
        }
        public AndLabelFilterBuilder and(ILabelFilter filter){
            andLabelFilter.add(filter);
            return this;
        }

        public OrLabelFilterBuild or(ILabelFilter filter){
            return s.or().or(filter);
        }
    }

    public static class OrLabelFilterBuild{
        private final LabelFilterBuilder s;
        private final OrLabelFilter orLabelFilter;

        public OrLabelFilterBuild(LabelFilterBuilder s, OrLabelFilter orLabelFilter) {
            this.s = s;
            this.orLabelFilter = orLabelFilter;
        }

        public LabelFilterBuilder end(){
            return s;
        }
        public OrLabelFilterBuild or(ILabelFilter filter){
            this.orLabelFilter.add(filter);
            return this;
        }

        public AndLabelFilterBuilder and(ILabelFilter filter){
            return s.and().and(filter);
        }
    }
}
