package cc.catman.plugin.event;

/**
 * 事件总线
 */
public interface IEventBus {

    void addEventPublishers(IEventPublisher<?,?> ...eventPublishers);
    void  addListener(IEventListener<?,?> listener);
    void publish(IEvent event);
}
