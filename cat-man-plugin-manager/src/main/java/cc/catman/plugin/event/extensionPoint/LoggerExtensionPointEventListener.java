package cc.catman.plugin.event.extensionPoint;

import cc.catman.plugin.event.AbstractEventListener;
import cc.catman.plugin.event.ObjectEventAck;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LoggerExtensionPointEventListener extends AbstractEventListener<ExtensionPointEvent, ObjectEventAck> {
    public LoggerExtensionPointEventListener() {
        super(Arrays.stream(ExtensionPointEventName.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @Override
    public ObjectEventAck handler(ExtensionPointEvent event) {
        log.info("[{}] - {}",event.getEventName(),event.getStandardPluginDescribe());
        return ObjectEventAck.empty();
    }
}
