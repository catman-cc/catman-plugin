package cc.catman.plugin.event;

/**
 * 事件发送者
 */
public interface IEventPublisher<T extends IEvent,R extends EventAck<?>> {

    IEventContext<T,R> getEventContext();

    boolean supports(IEvent event);

    T covert(IEvent event);

    void publish(IEvent event);
}
