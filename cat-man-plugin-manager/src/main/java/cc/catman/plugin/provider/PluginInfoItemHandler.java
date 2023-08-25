package cc.catman.plugin.provider;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class PluginInfoItemHandler {
    protected String kind;
    protected String source;
    /**
     * 解析处理一个插件信息列表
     * @param pluginInfoItem 插件项信息
     */
    public List<PluginInfoItem> handler(PluginInfoItem pluginInfoItem){
    if (!matchSource(pluginInfoItem.getSource())||matchKind(pluginInfoItem.getKind())){
        return Collections.singletonList(pluginInfoItem);
    }
    // 判断当前地址对应的文件是什么资源
        URI uri=pluginInfoItem.getLocation();
        Path p= Paths.get(uri);
        File f= p.toFile();
        if ( f.isDirectory()){
            // f是一个目录,因为是递归扫描,所以目录的话可以直接忽略掉
            return Collections.singletonList(pluginInfoItem);
        }
        // 如果f是一个文件的话,那就转换kind的值,改为file后,然后交回去继续处理
        return  Collections.singletonList(
                pluginInfoItem.next(pluginInfoItem.getSource()
                        , pluginInfoItem.getKind())
        );
    }

    public boolean matchKind(String kind){
        return this.kind.equals(kind.trim());
    }
    public boolean matchSource(String source){
        return this.source.equals(source.trim());
    }
}
