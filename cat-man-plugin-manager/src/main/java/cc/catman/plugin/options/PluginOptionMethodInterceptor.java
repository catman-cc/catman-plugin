package cc.catman.plugin.options;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 插件参数的方法拦截器
 */
public class PluginOptionMethodInterceptor implements MethodInterceptor {
    /**
     * 忽略的方法
     */
    public static final List<String> IGNORED_METHOD_NAMES = Arrays.asList(
            "getInherit"
            , "getParent"
            , "isAllInherit"
    );

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 因为方法的目的是调用parent的相关方法,所以这里先判断有没有parent
        Object result = proxy.invokeSuper(obj, args);
        if (result != null) {
            return result;
        }
        PluginOptions po = (PluginOptions) obj;
        return Optional.ofNullable(po.getParent()).map(parent -> {
            // 解析参数的方法名称,转换为对应的定义,主要是拦截所有get和is方法.
            // 所有的get和is方法都可以访问其
            String methodName = method.getName();
            if (IGNORED_METHOD_NAMES.stream().anyMatch(name-> name.equals(methodName))){
                return null;
            }

            String newMethodName = methodName;
            if (methodName.startsWith("get")) {
                newMethodName = methodName.substring(3);
            }
            if (methodName.startsWith("is")) {
                newMethodName = methodName.substring(2);
            }
            if (!newMethodName.equals(methodName)
                && (po.isAllInherit() || po.contain(newMethodName))
            ) {
                try {
                    return proxy.invoke(parent, args);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }).orElse(result);
    }
}
