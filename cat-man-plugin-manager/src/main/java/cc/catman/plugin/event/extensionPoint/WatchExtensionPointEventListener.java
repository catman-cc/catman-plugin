package cc.catman.plugin.event.extensionPoint;

import cc.catman.plugin.event.AbstractEventListener;
import cc.catman.plugin.event.Event;
import cc.catman.plugin.event.ObjectEventAck;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatchExtensionPointEventListener  extends AbstractEventListener<ExtensionPointInfoEvent, ObjectEventAck> {
    protected List<ExtensionPointInfo> watchs;

    public void add(ExtensionPointInfo extensionPointInfo){
        watchs.add(extensionPointInfo);
    }
    public WatchExtensionPointEventListener() {
        super(Arrays.asList(ExtensionPointEventName.WILL_STOP.name(),ExtensionPointEventName.CHANGE.name(),ExtensionPointEventName.INSTANCE.name()));
        this.watchs=new ArrayList<>();
    }

    @Override
    public ObjectEventAck handler(ExtensionPointInfoEvent event) {
        return ObjectEventAck.empty();
    }

    @Event("WILL_STOP")
    public ObjectEventAck stop(ExtensionPointInfoEvent event){
        if (watchs.contains(event.getExtensionPointInfo())){
            return onStop(event );
        }
        return ObjectEventAck.empty();
    }

    protected ObjectEventAck onStop(ExtensionPointInfoEvent event) {
        return ObjectEventAck.empty();
    }

    @Event("CHANGE")
    public ObjectEventAck change(ExtensionPointInfoEvent event){
        if (watchs.contains(event.getExtensionPointInfo())){
            return onChange(event );
        }
        return ObjectEventAck.empty();
    }

    private ObjectEventAck onChange(ExtensionPointInfoEvent event) {
        return ObjectEventAck.empty();
    }

    @Event("INSTANCE")
    public ObjectEventAck instance(ExtensionPointInfoEvent event){
        if (watchs.contains(event.getExtensionPointInfo())){
            return onInstance(event );
        }
        return ObjectEventAck.empty();
    }

    private ObjectEventAck onInstance(ExtensionPointInfoEvent event) {
        return ObjectEventAck.empty();
    }
}
