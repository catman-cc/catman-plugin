package cc.catman.plugin.describe;

import cc.catman.plugin.common.GAV;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;
import java.util.function.Consumer;

/**
 * 标准的插件描述对象
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardPluginDescribe extends BasicPluginDescribe {

    protected boolean normalDependencyInitializationCompleted;
    /**
     * 运行时,所依赖的插件
     */
    @Builder.Default
    protected List<StandardPluginDescribe> dependencies = new ArrayList<>();

    @Builder.Default
    protected List<String> ordinaryDependencyLibraryPaths=new ArrayList<>();

    protected List<String> normalClassLoadingStrategy;

    /**
     * 注册一组解析后置处理器,当PluginParser将其转换为ParserInfo时,调用
     */
    @JsonIgnore
    @Builder.Default
    protected List<Consumer<StandardPluginDescribe>> afterParsers = new ArrayList<>();

    @JsonIgnore
    @Builder.Default
    protected List<Consumer<PluginParseInfo>> afterHandlers = new ArrayList<>();

    /**
     * 插件对应的配置数据
     */
    protected Map<String, Object> properties;

    public void copyFrom(StandardPluginDescribe standardPluginDescribe) {
        this.setRelativePath(standardPluginDescribe.getRelativePath());
        this.setResource(standardPluginDescribe.getResource());
        this.setProperties(standardPluginDescribe.getProperties());
    }

    public StandardPluginDescribe addAfterParsers(Consumer<StandardPluginDescribe>... consumer) {
        this.afterParsers.addAll(Arrays.asList(consumer));
        return this;
    }

    public StandardPluginDescribe addAfterHandler(Consumer<PluginParseInfo>... consumer) {
        this.afterHandlers.addAll(Arrays.asList(consumer));
        return this;
    }

    public void callAfterParsers() {
        new ArrayList<>(this.afterParsers).forEach(consumer -> {
            consumer.accept(this);
        });
    }

    public void callAfterHandlers(PluginParseInfo parseInfo) {
        new ArrayList<>(this.afterHandlers).forEach(consumer -> {
            consumer.accept(parseInfo);
        });
    }
}
