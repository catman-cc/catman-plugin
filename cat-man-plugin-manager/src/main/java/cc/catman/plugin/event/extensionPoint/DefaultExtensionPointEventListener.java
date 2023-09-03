package cc.catman.plugin.event.extensionPoint;

import cc.catman.plugin.event.AbstractEventListener;
import cc.catman.plugin.event.Event;
import cc.catman.plugin.event.ObjectEventAck;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class DefaultExtensionPointEventListener extends AbstractEventListener<ExtensionPointInfoEvent, ObjectEventAck> {
    public DefaultExtensionPointEventListener() {
        super(Arrays.stream(ExtensionPointEventName.values()).map(Enum::name).collect(Collectors.toList()));
    }

    @Override
    public ObjectEventAck handler(ExtensionPointInfoEvent event) {
        return ObjectEventAck.empty();
    }
    @Event("FIND_EXTENSION_POINT_START")
    public ObjectEventAck start(ExtensionPointInfoEvent event){
        return ObjectEventAck.empty();
    }

    @Event("FIND_EXTENSION_POINT_END")
    public ObjectEventAck end(ExtensionPointInfoEvent event){
        return ObjectEventAck.empty();
    }

    @Event("FOUND")
    public ObjectEventAck onFound(ExtensionPointInfoEvent event){
        log.debug("find new extension point[{}],in:{}",event.getExtensionPointInfo().getClassName(),event.getStandardPluginDescribe().toGAV());
        return ObjectEventAck.empty();
    }

    @Event("CHANGE")
    public ObjectEventAck onChange(ExtensionPointInfoEvent event){
        return ObjectEventAck.empty();
    }

    @Event("INSTANCE")
    public ObjectEventAck onInstance(ExtensionPointInfoEvent event){
        return ObjectEventAck.empty();
    }

    @Event("WILL_STOP")
    public ObjectEventAck willStop(ExtensionPointInfoEvent event){
        return ObjectEventAck.empty();
    }
}
