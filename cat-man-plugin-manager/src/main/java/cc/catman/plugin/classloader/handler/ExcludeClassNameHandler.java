package cc.catman.plugin.classloader.handler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import cc.catman.plugin.classloader.matcher.DefaultClassNameMatcher;
import cc.catman.plugin.classloader.matcher.IClassNameMatcher;

/**
 * 用于排除指定类的加载器
 */
public class ExcludeClassNameHandler implements IClassLoaderHandler {

    protected static ConcurrentHashMap<String, ExcludeClassNameHandler> groupedExcludeClassNameInterceptors = new ConcurrentHashMap<>();

    protected IClassNameMatcher classNameMatcher;

    public ExcludeClassNameHandler(IClassNameMatcher classNameMatcher) {
        this.classNameMatcher = classNameMatcher;
    }

    public static ExcludeClassNameHandler create() {
        return new ExcludeClassNameHandler(new DefaultClassNameMatcher());
    }

    public static ExcludeClassNameHandler create(IClassNameMatcher matcher) {
        return new ExcludeClassNameHandler(matcher);
    }

    public static ExcludeClassNameHandler createOrGet(String name) {
        return groupedExcludeClassNameInterceptors.computeIfAbsent(name, k -> {
            return ExcludeClassNameHandler.create();
        });
    }

    public static ExcludeClassNameHandler createOrGet(String name,
            Function<String, ExcludeClassNameHandler> createFunction) {
        return groupedExcludeClassNameInterceptors.computeIfAbsent(name, createFunction);
    }

    public IClassNameMatcher getClassNameMatcher() {
        return this.classNameMatcher;
    }

    @Override
    public Payload before(Payload payload) {
        // 如果有需要排除的类,那么就不继续加载
        return payload.setContinue(!this.classNameMatcher.match(payload.getClassName()));
    }

    @Override
    public Payload after(Payload payload) {
        return payload;
    }

}
