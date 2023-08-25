package cc.catman.plugin.provider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PropFilePluginInfoItemHandler extends PluginInfoItemHandler{

    protected  List<String> supportNames=new ArrayList<>();
    @Override
    public List<PluginInfoItem> handler(PluginInfoItem pluginInfoItem) {
        // 判断插件是否包含文件描述信息,如果包含文件,才处理
        Path p=pluginInfoItem.getPath();
        if (pluginInfoItem.file==null
                || !pluginInfoItem.getFile().getName().endsWith(".properties")){
            return Collections.singletonList(pluginInfoItem);
        }
        // 这里需要考虑到该文件的全名
       if ( !supportNames.contains(pluginInfoItem.getFile().getName())){
           return Collections.singletonList(pluginInfoItem);
       }
        // 读取properties数据,然后进行解析
        Properties properties=new Properties();
        try {
            properties.load(pluginInfoItem.getLocation().toURL().openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicReference<String> kind=new AtomicReference<>();
        AtomicReference<String> source=new AtomicReference<>();
        Map<String,String> info=new HashMap<>();
        properties.keySet().forEach(k->{
            String nk=Objects.toString(k);
            String v=properties.getProperty(nk);
            if (null==v){
                // 需要抛出异常?
            }
            switch (nk){
                case "kind":{
                    kind.set(v);
                    break;
                }
                case "source":{
                    source.set(v);
                    break;
                } default:{
                    info.put(nk,v);
                }
            }
        });


        PluginInfoItem np=pluginInfoItem.next(source.get(),kind.get());
        np.setInformation(info);

        // 这样我们就获取了一个插件的描述内容,这时候的描述内容,有可能是直接执行一个插件,也有可能是一个插件列表的描述信息
        // 所以还需要判断当前插件是一个固定的插件信息,还是插件列表.
        // 接下来需要从infomation中读取几个固定的值.
        // 1. 判断当前是否是一个插件清单,如果是插件清单的话,按照插件清单处理
        if (np.getKind()=="list"){
            // properties格式表达插件清单比较混乱,因此直接就不支持
            throw  new RuntimeException("TODO ");
        }
        // 2. 如果是普通插件,先获描述对象,普通插件描述文件,里面有一个相对路径: RelativePath

        // 获取该字段的值,然后得到插件的跟地址
        return Collections.singletonList(np);
    }
}
