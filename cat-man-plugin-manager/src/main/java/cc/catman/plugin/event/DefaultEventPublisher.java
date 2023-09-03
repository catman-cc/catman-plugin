package cc.catman.plugin.event;

import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;

public class DefaultEventPublisher<T extends IEvent,R extends EventAck<?>>
        extends ParameterizedTypeReference<T>
        implements IEventPublisher<T,R>
{

    @Getter
    protected IEventContext<T,R> eventContext;

    public DefaultEventPublisher(IEventContext<T, R> eventContext) {
        this.eventContext = eventContext;
    }

    @Override
    public boolean supports(IEvent event) {
        try {
            covert(event);
        }catch (ClassCastException ignored){
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T covert(IEvent event) {
        return (T) event;
    }

    @Override
    public void publish(IEvent event) {
        eventContext.publish(covert(event));
    }
}
