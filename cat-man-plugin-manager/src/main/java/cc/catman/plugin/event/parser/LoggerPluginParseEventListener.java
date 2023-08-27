package cc.catman.plugin.event.parser;

import cc.catman.plugin.event.AbstractEventListener;
import cc.catman.plugin.event.ObjectEventAck;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class LoggerPluginParseEventListener extends AbstractEventListener<PluginParseEvent, ObjectEventAck> {

    public LoggerPluginParseEventListener() {
        super(Arrays.stream(EPluginParseEventName.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @Override
    public ObjectEventAck handler(PluginParseEvent event) {

        if (EPluginParseEventName.valueOf(event.getEventName()) == EPluginParseEventName.WARN_CAN_NOT_FOUND_HANDLE) {
            log.warn("can not found ParseInfoHandle for: {}", event.getParseInfo());
            return ObjectEventAck.empty();
        }
        log.debug("A new plugin parse info: {}", event.getParseInfo());
        return ObjectEventAck.empty();
    }
}
