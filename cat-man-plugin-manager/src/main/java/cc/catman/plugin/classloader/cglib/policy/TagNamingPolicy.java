package cc.catman.plugin.classloader.cglib.policy;

import net.sf.cglib.core.DefaultNamingPolicy;

public class TagNamingPolicy extends DefaultNamingPolicy {
    private final String tag;

    public TagNamingPolicy(String tag) {
        this.tag = tag;
    }

    @Override
    protected String getTag() {
        return super.getTag()+"_"+tag+"_";
    }

    public boolean hasTag(String name){
        return name.contains(getTag());
    }
}