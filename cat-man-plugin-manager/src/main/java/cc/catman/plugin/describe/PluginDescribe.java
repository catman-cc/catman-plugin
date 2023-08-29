package cc.catman.plugin.describe;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * 最基础的插件描述信息,用于描述插件的源和插件的类型
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
     * 插件的基础地址
     */
    protected String baseDir;

    /**
     * 插件对应的资源描述
     */
    protected Resource describeResource;

    /**
     * 普通依赖项资源对应的文件类型
     */
    protected String normalDependencyType;

    /**
     * 运行时,需要使用的第三方库,这里的第三方库并不是插件,只是普通的jar包
     */
    protected Resource normalDependencyLibrariesResource;
}
