package cc.catman.plugin.describe;

import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.classloader.configuration.IClassLoaderConfiguration;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import cc.catman.plugin.describe.handler.IPluginParserInfoHandler;
import cc.catman.plugin.describe.parser.IPluginDescribeParser;
import cc.catman.plugin.describe.parser.IPluginParserContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
    @JsonIgnore
    private boolean initialized;
    /**
     * 用于解析改插件描述对象的解析器
     */
    @JsonIgnore
    private IPluginDescribeParser parser;
    /**
     * 插件的解析状态
     */
    private EPluginParserStatus status;
    @JsonIgnore
    private IClassLoaderConfiguration classLoaderConfiguration;
    @JsonIgnore
    private IPluginParserContext pluginParserContext;
    @JsonIgnore
    private ClassLoader classLoader;
    @JsonIgnore
    private IPluginInstance pluginInstance;
    @JsonIgnore
    @Builder.Default
    private List<IPluginParserInfoHandler> handlerChain=new ArrayList<>();

    private List<String> orderlyClassLoadingStrategy;

    void updatePluginInstance(IPluginInstance pluginInstance){
        this.pluginInstance=pluginInstance;
        if (null!=pluginInstance){
            pluginInstance.setPluginParseInfo(this);
        }
    }
    public <T extends PluginParseInfo> T decode(Class<T> clazz){
        T decode = this.parser.decode(this, clazz);
        decode.setParser(parser);
        decode.setInitialized(true);
        decode.updatePluginInstance(pluginInstance);
        decode.setClassLoaderConfiguration(classLoaderConfiguration);
        decode.setPluginParserContext(pluginParserContext);
        decode.setClassLoader(classLoader);
        decode.setHandlerChain(this.getHandlerChain());
        return decode;
    }
}
