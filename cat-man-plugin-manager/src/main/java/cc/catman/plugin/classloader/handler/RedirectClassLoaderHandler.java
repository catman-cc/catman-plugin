package cc.catman.plugin.classloader.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import cc.catman.plugin.classloader.matcher.DefaultClassNameMatcher;
import cc.catman.plugin.classloader.matcher.IClassNameMatcher;

/**
 * 用于将指定类分发至特定类加载器,比如,jdk自带的类型,必须使用jdk的类加载器
 *
 * Note. 这是一个很重要的功能,如果禁用了该功能,那么插件将无法加载系统自带的类定义
 */
public class RedirectClassLoaderHandler implements IClassLoaderHandler {
    public static final String[] JDK_EXCLUDED_PACKAGES = new String[] { "apple.",
            "com.apple", "java.", "javax.", "jdk", "sun.",
            "com.sun",
            "oracle.",
            "javassist.", "org.aspectj.", "net.sf.cglib." };

    protected List<RedirectRule> redirectRules = createRedirectRules();

    private List<RedirectRule> createRedirectRules() {
        redirectRules = new ArrayList<>();
        redirectRules.add(new RedirectRule( DefaultClassNameMatcher.of(JDK_EXCLUDED_PACKAGES), (payload) -> {
            try {
                Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(payload.getClassName());
                payload.setClazz(clazz);

            } catch (ClassNotFoundException e) {
                payload.setError(e);
            }
            return payload.setContinue(false);

        }));
        return redirectRules;
    }

    @Override
    public Payload before(Payload payload) {
        for (RedirectRule rule : this.redirectRules) {
            if (rule.match(payload.getClassName())) {
                return rule.apply(payload);
            }
        }
        return payload;
    }

    public static class RedirectRule {
        private IClassNameMatcher matcher;
        private Function<Payload, Payload> apply;

        public RedirectRule(IClassNameMatcher matcher, Function<Payload, Payload> apply) {
            this.matcher = matcher;
            this.apply = apply;
        }

        public boolean match(String name) {
            return this.matcher.match(name);
        }

        public Payload apply(Payload payload) {
            return this.apply.apply(payload);
        }
    }

}
