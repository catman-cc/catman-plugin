package cc.catman.plugin.describe;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class JarPluginDescribe extends PluginDescribe{

    /**
     * jar类型插件使用的扩展lib地址,可以有多个,理论上地址的必须是该jar插件的子目录
     */
    protected List<String> libAntPatterns=new ArrayList<>();

}
