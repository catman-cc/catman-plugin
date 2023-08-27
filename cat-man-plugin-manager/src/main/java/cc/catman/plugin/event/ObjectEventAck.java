package cc.catman.plugin.event;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ObjectEventAck extends EventAck<Object>{
    public static ObjectEventAck empty(){
        return ObjectEventAck.builder().build();
    }
}
