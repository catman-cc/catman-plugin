package cc.catman.plugin.provider;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基本的插件信息项
 */
@Builder
@Data
public class PluginInfoItem {
    /**
     * 插件的类型,比如:list, jar,fat-jar,jar-with-dir,source-code,zip,dir,等等
     * 每一个kind都会对应一个解析器,解析器在处理时,能够在解析时,判断自己是否生成完整的插件,或者需要传递给其他的解析器处理.
     */
    protected  String kind;
    /**
     * 插件的来源,比如: 本地,数据库,git等等
     */
    protected String source;
    /**
     * 插件解析过程中的信息
     */
    protected List<PluginInfoItem> sourceChain=new ArrayList<>();

    protected URI location;

    protected File file;

    protected Path path;

    /**
     * 这里是一些额外的信息,主要目的是为某些特定的转换机提供内容,
     * 比如,source为 git的工程,拉下来代码之后,需要将source改为local,
     * 然后需要在information中添加local的地址,然后maven或者gradle插件才可以去读取information中的本地地址,进行编译.
     * 编译完成之后,maven/gradle插件可以保持source不变,然后将kind改为jar/fat-jar之类的内容,然后再交给下一个转换器进行处理,
     * 最后生成一个或多个包含了类加载器的完整的PluginDesc.
     */
    protected Map<String,String> information;

    public PluginInfoItem next(String source,String kind){
        List<PluginInfoItem> list = new ArrayList<>(this.sourceChain);
        list.add(this);
        return PluginInfoItem.builder()
                .source(source)
                .kind(kind)
                .location(this.getLocation())
                .path(this.getPath())
                .file(this.getFile())
                .information(this.getInformation())
                .sourceChain(list)
                .build();
    }
}
