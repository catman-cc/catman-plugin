package cc.catman.plugin.core.describe;

import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.common.DescribeMapper;
import cc.catman.plugin.enums.EPluginParserStatus;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.runtime.IPluginInstance;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;
import java.util.function.Consumer;

/**
 * 插件的解析信息,该实例分为三个状态:
 * 1. 初始化,初始化时,该实例只有kind,source,parser,classLoaderConfiguration,pluginParserContext,pluginDescribe,这些基本数据
 *    {@link cc.catman.plugin.describe.parser.AbstractJacksonPluginDescribeParser#wrapper(StandardPluginDescribe)}
 * 2. 完成初始化后,该插件会在运行时,被某一个具体的{@link IPluginParserInfoHandler}实现类将pluginDescribe进一步解析,同时修改status的值.
 * 3. 如果可以的话,{@link IPluginParserInfoHandler}实现类会填充classLoader字段,至此,PluginParseInfo的字段被填充完毕,后续的操作开始
 *    由classLoader的代理对象实现.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PluginParseInfo extends StandardPluginDescribe {

    @Builder.Default
    private String lifeCycle= ELifeCycle.UNKNOWN.name();

    @Builder.Default
    private Queue<String> nextLifCycle=new ArrayDeque<>();

    /**
     *  如果没有配置特点的nextLifCycle,将会根据lifeCycleContinue来决定是否进入下一阶段.
     */
    @Builder.Default
    private boolean lifeCycleContinue=true;

    @JsonIgnore
    private boolean initialized;

    @Builder.Default
    private Map<String, Set<String>> lifeCycleTasks=new HashMap<>();

    /**
     * 插件的解析状态
     */
    private EPluginParserStatus status;
    @JsonIgnore
    private IClassLoaderConfiguration classLoaderConfiguration;

    @JsonIgnore
    private ClassLoader classLoader;
    @JsonIgnore
    private IPluginInstance pluginInstance;
    @JsonIgnore
    @Builder.Default
    private List<IPluginParserInfoHandler> handlerChain=new ArrayList<>();

    private List<String> orderlyClassLoadingStrategy;

    /**
     * 此处用于捕获所有未声明的配置信息
     */

    @JsonAnySetter
    private Map<String, Object> dynamicValues;
      public Set<String> getTasks(String lifeCycle){
        return this.lifeCycleTasks.getOrDefault(lifeCycle,new LinkedHashSet<>());
    }
    @JsonAnyGetter
    public Map<String, Object> getDynamicValues() {
        return dynamicValues;
    }

    void updatePluginInstance(IPluginInstance pluginInstance){
        this.pluginInstance=pluginInstance;
        if (null!=pluginInstance){
            pluginInstance.setPluginParseInfo(this);
        }
    }

    public PluginParseInfo addTask(String lifeCycle,String name){
        this.getLifeCycleTasks().computeIfAbsent(lifeCycle,(k)-> new HashSet<>())
                .add(name);
        return this;
    }

    public PluginParseInfo nextLifeCycle(String lifeCycle){
        this.nextLifCycle.add(lifeCycle);
        return this;
    }
    public <T extends PluginParseInfo> T decode(Class<T> clazz){
        // 使用ModelMapper做类型转换
        T decode= DescribeMapper.getMapper().map(this,clazz);
        decode.updatePluginInstance(pluginInstance);
        return decode;
    }


}
