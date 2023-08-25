package cc.catman.plugin.event.error;

import cc.catman.plugin.event.EventAck;
import cc.catman.plugin.event.IEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 错误事件监听器
 */
@Slf4j
public class ErrorEventListener implements IEventListener<ErrorEvent, EventAck<Throwable>> {

    @Override
    public List<String> getWatchNames() {
       return Arrays.stream(ErrorEventName.values()).map(Enum::name).collect(Collectors.toList());
    }

    @Override
    public EventAck<Throwable> handler(ErrorEvent event) {

        return null;
    }
}
