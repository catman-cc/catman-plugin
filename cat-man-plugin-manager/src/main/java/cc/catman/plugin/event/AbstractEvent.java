package cc.catman.plugin.event;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public abstract class AbstractEvent implements IEvent{

    @Getter
    @Setter
    protected String eventName;

    public AbstractEvent() {
    }

    public AbstractEvent(String eventName) {
        this.eventName = eventName;
    }
}
