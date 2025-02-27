package cc.catman.plugin.handlers.search;

import cc.catman.plugin.core.describe.StandardPluginDescribe;
import org.springframework.core.io.Resource;

public abstract class FinderJob {
    private boolean done;

    public boolean isDone(){
        return done;
    }

    public boolean notDone(){
        return !isDone();
    }

    public  boolean exec(StandardPluginDescribe pluginDescribe, Resource resource){
        if (!done){
            this.done=process(pluginDescribe,resource);
        }
        return this.done;
    }
    protected abstract boolean process(StandardPluginDescribe pluginDescribe,Resource resource);
}
