package cc.catman.plugin.classloader.matcher;

public class ReversalClassNameMatcher implements IClassNameMatcher{
    private IClassNameMatcher matcher;

    public ReversalClassNameMatcher(IClassNameMatcher matcher) {
        this.matcher = matcher;
    }
    public static ReversalClassNameMatcher wrapper(IClassNameMatcher matcher){
        return  new ReversalClassNameMatcher(matcher);
    }
    @Override
    public boolean match(String name) {
        return !matcher.match(name);
    }
}
