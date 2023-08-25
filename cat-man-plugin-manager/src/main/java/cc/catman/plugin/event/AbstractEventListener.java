package cc.catman.plugin.event;

import lombok.Getter;

import java.util.List;

public abstract class AbstractEventListener<T extends IEvent,R extends EventAck<?>> implements IEventListener<T,R>{

    @Getter
    protected List<String> watchNames;

    public AbstractEventListener(List<String> watchNames) {
        if (watchNames==null){
            // 抛出异常
        }
        this.watchNames = watchNames;
    }
}
