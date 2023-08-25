package cc.catman.plugin.classloader.handler;

/**
 * 类加载拦截器,可以进行前后处理
 */
public interface IClassLoaderHandler {

    default Payload before(Payload payload) {
        return payload;
    }

    default Payload after(Payload payload) {
        return payload;
    }
}
