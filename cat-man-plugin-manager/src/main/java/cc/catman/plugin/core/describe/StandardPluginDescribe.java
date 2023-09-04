package cc.catman.plugin.core.describe;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.runtime.IPluginInstance;
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
    @JsonIgnore
    protected boolean normalDependencyInitializationCompleted;
    /**
     * 运行时,所依赖的插件
     */
    @Builder.Default
    protected List<PluginParseInfo> dependencies = new ArrayList<>();
    @JsonIgnore
    protected StandardPluginDescribe from;

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

    @Builder.Default
    @Getter
    @JsonIgnore
    protected Stack<Consumer<IPluginInstance>> unInstallFunc =new Stack<>();

    /**
     * 插件对应的配置数据
     */
    protected Map<String, Object> properties;


    public void copyFrom(StandardPluginDescribe standardPluginDescribe) {
        this.setRelativePath(standardPluginDescribe.getRelativePath());
        this.setDescribeResource(standardPluginDescribe.getDescribeResource());
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
    public StandardPluginDescribe addOnUninstallFunction(Consumer<IPluginInstance> f){
        this.unInstallFunc.push(f);
        return this;
    }

    public void callOnUnInstallFunctions(IPluginInstance instance){
        unInstallFunc.forEach(f->{
            f.accept(instance);
        });
        Optional.ofNullable(from).ifPresent(from->{
            from.callOnUnInstallFunctions(instance);
        });
    }

    public boolean matchGAV(GAV gav){
        if (toGAV().equals(gav)){
            return true;
        }
       return Optional.ofNullable(from).map(f->f.matchGAV(gav)).orElse(false);
    }
}
