package cc.catman.plugin.event;

import java.util.*;

public class DefaultIEventContext<T extends IEvent,R extends EventAck<?>> implements IEventContext<T,R> {

    /**
     * 事件的接受者
     */
    protected Map<String, List<IEventListener<T,R>>> listeners;

    public DefaultIEventContext() {
        this.listeners=new HashMap<>();
    }

    @Override
    public void addListener(IEventListener<T,R> listener) {
        listener.getWatchNames().forEach(name->{
            listeners.computeIfAbsent(name,(n)-> new ArrayList<>())
                    .add(listener);

        });
    }

    @Override
    public void publish(T event) {
        String eventName = event.getEventName();
        for (IEventListener<T,R> listener : listeners.getOrDefault(eventName, Collections.emptyList())) {
           R ack= listener.handler(event);
           if ( Optional.ofNullable(ack).isPresent()){
               if (ack.isStopBroadcasting()){
                   // 中断广播
                   return;
               }
           }
        }
    }
}