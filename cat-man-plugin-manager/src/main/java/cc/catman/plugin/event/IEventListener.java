package cc.catman.plugin.event;

import java.util.List;

/**
 * 事件监听器,处理监听到的时间
 * @param <T> 事件实例
 */
public interface IEventListener<T extends IEvent,R extends EventAck<?>> {

    List<String> getWatchNames();
    R handler(T event);
}
