package cc.catman.plugin.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultEventBus implements IEventBus {
    protected List<IEventPublisher<?, ?>> eventPublishers;
    protected List<IEventListener<?, ?>> listeners;

    public DefaultEventBus() {
        this.eventPublishers = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void addEventPublishers(IEventPublisher<?, ?>... eventPublishers) {
        for (IEventPublisher<?, ?> publisher : eventPublishers) {
            IEventContext<?, ?> eventContext = publisher.getEventContext();
            for (IEventListener<?, ?> listener : listeners) {
                eventContext.tryAddListener(listener);
            }
        }
        this.eventPublishers.addAll(Arrays.asList(eventPublishers));
    }

    @Override
    public void addListener(IEventListener<?, ?> listener) {
        this.listeners.add(listener);
        for (IEventPublisher<?, ?> publish : eventPublishers) {
            IEventContext<?, ?> eventContext = publish.getEventContext();
            eventContext.tryAddListener(listener);
        }
    }

    @Override
    public void publish(IEvent event) {
        for (IEventPublisher<?, ?> publish : eventPublishers) {
            if (publish.supports(event)) {
                publish.publish(event);
            }
        }
    }
}
