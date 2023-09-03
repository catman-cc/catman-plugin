package cc.catman.plugin.event;

import org.springframework.util.TypeUtils;

public interface IEventContext<T extends IEvent,R extends EventAck<?>> {

    @SuppressWarnings("unchecked")
    default boolean tryAddListener(IEventListener<?,?> listener){
        try {
            IEventListener<T,R> l=(IEventListener<T,R>)listener;
            addListener(l);
            return true;
        }catch (ClassCastException ignored){}
        return false;
    }

    /**
     * 添加一个新的监听器
     */
    void  addListener(IEventListener<T,R> listener);

    void removeListener(IEventListener<?,?> listener);

    void publish(T event);
}
