package cc.catman.plugin.event;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 * 需要注意 ,经过包装后,原始的handler方法将会作为 保底方法执行
 */
public class EventListenerBuilder<T extends IEvent, R extends EventAck<?>> {
    protected Set<String> watchNames=new HashSet<>();
    Map<String, Function<T, R>> functionMap = new HashMap<>();

    Function<T, R> defaultInvoke;

    public EventListenerBuilder(Function<T, R> defaultInvoke) {
        this.defaultInvoke = defaultInvoke;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEvent, R extends EventAck<?>> IEventListener<T, R> wrapper(IEventListener<T, R> listener) {
        EventListenerBuilder<T, R> builder = new EventListenerBuilder<>(listener::handler);
        builder.watchNames.addAll(listener.getWatchNames());
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(listener.getClass());
        Arrays.stream(allDeclaredMethods).forEach(method -> {
            Event e = AnnotationUtils.findAnnotation(method, Event.class);
            if (e == null) {
                return;
            }
            builder.watchNames.add(e.value());
            builder.addHandler(e.value(), (T) -> {
                try {
                    return (R) method.invoke(listener, T);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });
        return builder.build();
    }

    public EventListenerBuilder<T, R> addHandler(String key, Function<T, R> invoke) {
        functionMap.put(key, invoke);
        return this;
    }

    public IEventListener<T, R> build() {
        return new IEventListener<T, R>() {
            @Override
            public List<String> getWatchNames() {
                return new ArrayList<>(watchNames);
            }

            @Override
            public R handler(T event) {
                return Optional.ofNullable(functionMap.get(event.getEventName()))
                        .orElse(defaultInvoke)
                        .apply(event);
            }
        };
    }
}
