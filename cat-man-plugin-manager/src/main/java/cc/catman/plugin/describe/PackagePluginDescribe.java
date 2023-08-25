package cc.catman.plugin.describe;

import cc.catman.plugin.describe.enmu.EPluginKind;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class PackagePluginDescribe extends PluginDescribe {
    public static final String KIND= EPluginKind.PACKAGE.name();

    /**
     * 包类型的插件描述中的items元素,包含了一组插件描述数据
     */
    protected List<PluginDescribe> items;

    public static PackagePluginDescribe of(PluginDescribe pluginDescribe){
        PackagePluginDescribe ppd=new PackagePluginDescribe();
        ppd.copyFrom(pluginDescribe);
        ppd.setKind(KIND);

        // 接下来是初始化自己的内容,即读取settings的数据,将其转换为自定义数据
        Field field = ReflectionUtils.findField(PackagePluginDescribe.class, "items");

//        List<PluginDescribe> is= (List<PluginDescribe>) ppd.getProperties().getProperty("items",field.getType());
//        System.out.println(is);
        return ppd;
    }
}
