package cc.catman.plugin.classloader.handler;

import java.util.ArrayList;
import java.util.List;

import cc.catman.plugin.classloader.exceptions.ClassLoadRuntimeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultClassLoaderHandlerChain implements IClassLoaderHandler {

    @Getter
    protected List<IClassLoaderHandler> handlers = new ArrayList<>();

    public DefaultClassLoaderHandlerChain addHandler(IClassLoaderHandler handler) {
        this.handlers.add(handler);
        return this;
    }

    public DefaultClassLoaderHandlerChain insertHandler(int index, IClassLoaderHandler handler) {
        this.handlers.add(index, handler);
        return this;
    }

    @Override
    public Payload before(Payload payload) {
        Payload p = payload;
        for (IClassLoaderHandler clh : handlers) {
            p = handlerChainError(clh.before(p));
            if (log.isTraceEnabled()) {
                log.trace("ClassLoaderHandler[{}] is processing a class loading request named: {}",
                        clh.getClass().getSimpleName(), payload.getClassName());
            }
            if (!p.isContinue()) {
                log.debug("class {} loading request was aborted, with:{}",
                         payload.getClassName(),clh.getClass().getSimpleName());
                return p;
            }
        }
        return p;
    }

    @Override
    public Payload after(Payload payload) {
        Payload p = payload;
        for (IClassLoaderHandler clh : handlers) {
            p = handlerChainError(clh.after(p));
            if (!p.isContinue()) {
                return p;
            }
        }
        return p;
    }

    protected Payload handlerChainError(Payload payload) {
        // 不继续执行,表示前面提供了其他值,需要获取到对应的错误信息
        if (payload.hasError()) {
            log.error("An error occurred when dealing with the class named: {},err:{}", payload.getClassName(),
                    payload.getError());
            throw new ClassLoadRuntimeException(payload.getError());
        }
        return payload;
    }
}
