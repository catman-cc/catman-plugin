package cc.catman.plugin.classloader.cglib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import cc.catman.plugin.classloader.context.ClassLoaderContext;
import cc.catman.plugin.classloader.exceptions.ClassLoadRuntimeException;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 可配置的类加载器代理
 */
public class ConfigurableClassLoaderMethodInterceptor implements MethodInterceptor {

    protected ClassLoaderContext classLoaderContext;

    protected Map<String, ProxyMethod> proxyMethods = new HashMap<>();

    protected ProxyMethodCreator defaultProxyMethodCreator = createDefaultProxyMethodCreator();

    protected ClassLoader proxyedClassLoader;

    public ConfigurableClassLoaderMethodInterceptor(ClassLoaderContext classLoaderContext,
            ClassLoader proxyedClassLoader) throws NoSuchMethodException {
        this.classLoaderContext = classLoaderContext;
        this.proxyedClassLoader = proxyedClassLoader;
        loadDefaultProxyMethod();
    }

    public void loadDefaultProxyMethod() throws NoSuchMethodException {
        createProxyMethodForLoadClass();
    }

    public ConfigurableClassLoaderMethodInterceptor createProxyMethodForLoadClass()
            throws NoSuchMethodException, SecurityException {
        Method loadClass = proxyedClassLoader.getClass().getMethod("loadClass", String.class);
        ProxyMethod pm = (o, a, p) -> {
            String className = (String) a[0];
            ClassLoader classLoader=(ClassLoader) o;
            return classLoaderContext.loadClass(className,classLoader ,(cn) -> {
                // TODO 需要在这里新增方法,允许加载类时,安装指定的顺序和策略加载,比如,优先加载 上级插件等.
                // 加载策略和IClassLoaderHandler的区别在于,加载策略不具备拦截操作,仅且可以用来按照不同的顺序加载类型定义
                // 这也意味着,加载策略的功能必须是固定的.
                try {
                    return (Class<?>) p.invokeSuper(o, new Object[] { cn });
                } catch (Throwable e) {
                    throw new ClassLoadRuntimeException(e);
                }
            });
        };
        addProxyMethod(loadClass, pm);
        return this;
    }

    private ProxyMethodCreator createDefaultProxyMethodCreator() {
        return new ProxyMethodCreator();
    }

    public ConfigurableClassLoaderMethodInterceptor addProxyMethod(Method method, ProxyMethod pm) {
        this.proxyMethods.put(method.toGenericString(), pm);
        return this;
    }

    public ConfigurableClassLoaderMethodInterceptor removeProxyMethod(Method method) {
        this.proxyMethods.remove(method.toGenericString());
        return this;
    }

    @FunctionalInterface
    public static interface ProxyMethod {
        public Object invoke(Object proxy, Object[] args, MethodProxy proxyMethod) throws Throwable;
    }

    public static class DefaultProxyMethod implements ProxyMethod {
        private Method m;

        public DefaultProxyMethod(Method m) {
            this.m = m;
        }

        public Object invoke(Object obj, Object[] args, MethodProxy proxyMethod) throws Throwable {
           return proxyMethod.invokeSuper(obj, args);
//            return m.invoke(obj, args, proxyMethod);
        }
    }

    public static class ProxyMethodCreator {
        public ProxyMethod create(Method m) {
            return new DefaultProxyMethod(m);
        }
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        ProxyMethod pm=Optional.ofNullable(proxyMethods.get(method.toGenericString()))
                .orElseGet(()->defaultProxyMethodCreator.create(method));
        return  pm.invoke(obj, args, proxy);
    }
}
