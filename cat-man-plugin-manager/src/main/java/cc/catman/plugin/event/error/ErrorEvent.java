package cc.catman.plugin.event.error;

import cc.catman.plugin.event.AbstractEvent;
import cc.catman.plugin.event.IEvent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ErrorEvent extends AbstractEvent implements IEvent {
    @Getter
    @Setter
    private Throwable error;

    public ErrorEvent(String eventName) {
        super(eventName);
    }

}
