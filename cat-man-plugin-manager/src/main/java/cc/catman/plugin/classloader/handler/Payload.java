package cc.catman.plugin.classloader.handler;

import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.context.ClassLoaderContext;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.function.Function;

@Data
@Accessors(chain = true)
public class Payload {
    /**
     * 类加载器上下文
     */
    private ClassLoaderContext context;

    /**
     * 类加载器
     */
    private ClassLoader classLoader;

    private IPluginInstance pluginInstance;

    /**
     * 需要加载的类名称
     */
    private String className;

    /**
     * 加载过程中抛出的异常
     */
    private Throwable error;

    /**
     * 当前阶段的加载链是否继续执行
     */
    private boolean isContinue;
    /**
     * 加载得到的类定义
     */
    private Class<?> clazz;

    private Function<String, Class<?>> selfLoadFunction;


    public boolean hasError() {
        return null != this.error;
    }
    public boolean loadedClass(){
        return null!=this.clazz;
    }

    public Payload resetContinue() {
        this.isContinue = true;
        return this;
    }

}
