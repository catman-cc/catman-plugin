package cc.catman.plugin.core.label;

import cc.catman.plugin.classloader.matcher.DefaultClassNameMatcher;
import cc.catman.plugin.common.Constants;
import cc.catman.plugin.classloader.cglib.policy.TagNamingPolicy;
import cc.catman.plugin.enums.ELabel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryLabel implements MethodInterceptor {
    private static final TagNamingPolicy NAMING_POLICY=new TagNamingPolicy("history_label");
    protected String historyName= ELabel.DEFAULT_HISTORY.v();

    protected static DefaultClassNameMatcher matcher=new DefaultClassNameMatcher()
            .addExcludePackage(Constants.JDK_EXCLUDED_PACKAGES)
            .addExcludePackage("junit");

    public static boolean isHistoryLabel(ILabelAbility labelAbility){
        return NAMING_POLICY.hasTag(labelAbility.getLabels().getClass().getCanonicalName());
    }

    public static  <T extends ILabelAbility> Class<T> wrapper(Class<T> c) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(c);
        enhancer.setNamingPolicy(NAMING_POLICY);
        enhancer.setCallback(new HistoryLabel());
        return (Class<T>) enhancer.createClass();
    }

    public static Labels  wrapper(Labels ls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Labels.class);
        enhancer.setNamingPolicy(NAMING_POLICY);
        enhancer.setCallback(new HistoryLabel());

        if ( ls.getLabels()==null){
            ls.setLabels(new Labels());
        }
        return (Labels) enhancer.create(new Class[]{Labels.class, Map.class},new Object[]{ls.getLabels(),ls.getItems()});
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String name = method.getName();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int last=-1;
        for (int i = 0; i < stackTrace.length; i++) {
            if (stackTrace[i].getClassName().startsWith("cc.catman.plugin.label.Labels$$EnhancerByCGLIB$$")){
                if (last+2==i) {
                    return proxy.invokeSuper(obj, args);
                }
                if (last!=-1&&last-i>1){
                    break;
                }
                last=i;
            }
        }

        if (name.startsWith("add")
        ||name.startsWith("replace")
                ||name.startsWith("remove")
                ||name.startsWith("rm")
        ){
            String operator = Arrays
                    .stream(stackTrace)
                    .skip(2)
                    .filter(se ->
                           ! matcher.match(se.getClassName())
                             && !se.getClassName().startsWith("cc.catman.plugin.label"))
                    .findFirst()
                    .map(se -> se.getClassName() + se.getMethodName() + "(" + se.getLineNumber() + ")")
                    .orElse("------");

            ((ILabelAbility)(obj)).labels()
                    .add(historyName, new History(name,Arrays.asList(args),System.currentTimeMillis(),operator).toJson());
        }
        return proxy.invokeSuper(obj,args);
    }

    @Data
    @AllArgsConstructor
    public static class History{
        protected String methodName;
        protected List<Object> args;
        protected long time;
        protected String operator;

        public String toJson(){
            return "{"
                   +ws("method")+":"+ws(methodName)+","
                   +ws("args")+": ["+args.stream().map(this::ws).collect(Collectors.joining(",")) +"],"
                   +ws("time")+":"+time+","
                   +ws("operator")+":"+operator+","
                    +"}";
        }
        public String ws(Object s){
            return "\""+s+"\"";
        }
    }
}
