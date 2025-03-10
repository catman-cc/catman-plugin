package cc.catman.plugin.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 事件消费回执
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventAck<T> {
    private T Ack;

    /**
     * 事件是否被消费
     */
    private boolean consumed;

    /**
     * 停止事件的广播
     */
    private boolean stopBroadcasting;
}
