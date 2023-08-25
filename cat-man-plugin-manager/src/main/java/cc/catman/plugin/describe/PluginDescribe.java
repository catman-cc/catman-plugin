package cc.catman.plugin.describe;

import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import lombok.Data;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 插件描述对象
 */
@Data
public class PluginDescribe {

    /**
     * 插件的来源,不同的来源可能对应着不同的处理方式
     * 比如:
     *    GIT,MYSQL,
     *    - GIT: 需要先获取代码,得到source_code类型的插件,然后交给其他插件处理.
     *    - MYSQL: 直接生成MYSQL对应的ClassLoader即可.
     * @see {@link EPluginSource}
     */
    protected String  source;

    /**
     * 插件的类型,比如:list, jar,fat-jar,jar-with-dir,source-code,zip,dir,等等
     * 每一个kind都会对应一个解析器,解析器在处理时,能够在解析时,判断自己是否生成完整的插件,或者需要传递给其他的解析器处理.
     * @see {@link EPluginKind}
     */
    protected String kind;


    /**
     * 插件描述文件和插件的相对路径,比如:../../ 表示父级目录的父级目录
     * Note,这里有一个安全问题,就是目录不能跳出项目配置的根目录.
     * 每个插件都必须有该属性
     */
    protected String relativePath="../";

    /**
     * 插件对应的资源描述
     */
    protected Resource resource;

    List<String> extensionsPoints=new ArrayList<>();

    protected List<PluginDescribe> dependencies=new ArrayList<>();

    protected List<PluginDescribe> systemDependencies=new ArrayList<>();

    protected List<String> orderlyClassLoadingStrategy;
    /**
     * 插件对应的配置数据
     */
    protected Map<String,Object> properties;

    public void copyFrom(PluginDescribe pluginDescribe){
//        this.setSource(pluginDescribe.getSource());
//        this.setKind(pluginDescribe.getKind());
        this.setRelativePath(pluginDescribe.getRelativePath());
        this.setResource(pluginDescribe.getResource());
        this.setProperties(pluginDescribe.getProperties());
    }

}
