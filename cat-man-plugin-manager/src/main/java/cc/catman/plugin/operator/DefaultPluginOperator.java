package cc.catman.plugin.operator;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.runtime.IPluginInstance;
import cc.catman.plugin.runtime.IPluginManager;
import cc.catman.plugin.runtime.PluginInstanceTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultPluginOperator implements IPluginOperator {
    protected IPluginManager pluginManager;

    protected PluginOperatorOptions pluginOperatorOptions;

    public DefaultPluginOperator(IPluginManager pluginManager, PluginOperatorOptions pluginOperatorOptions) {
        this.pluginManager = pluginManager;
        this.pluginOperatorOptions = pluginOperatorOptions;
    }

    public DefaultPluginOperator(IPluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.pluginOperatorOptions = PluginOperatorOptions.builder().build();
    }

    @Override
    public IPluginExtensionPointOperator createPluginExtensionPointVisitor() {
        return this.pluginManager.createPluginExtensionPointOperator(this);
    }

    @Override
    public PluginInstanceTree tree() {

        return tree(p -> true, PluginOperatorHelper.MAX_DEEP);
    }

    @Override
    public PluginInstanceTree tree(GAV gav, int deep) {
        return tree((pluginInstance -> GAV.builder().group(pluginInstance.getGroup()).name(pluginInstance.getName()).version(pluginInstance.getVersion()).build()
                .Match(gav)), deep);
    }

    @Override
    public PluginInstanceTree tree(Predicate<IPluginInstance> pluginFilter, int deep) {
        if (deep <= 0) {
            return null;
        }
        int finalDeep = --deep;
        return PluginInstanceTree.builder()
                .node(pluginManager.getOwnerPluginInstance())
                .tree(pluginManager.getPluginInstances().stream()
                        .filter(pluginOperatorOptions.wrapper(pluginFilter))
                        .map(pi -> pi.getPluginManager().createPluginVisitor(pluginOperatorOptions).tree(pluginFilter, finalDeep))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public void every(Predicate<IPluginInstance> pluginFilter, int deep, Consumer<IPluginInstance> visitor) {
        if (deep <= 0) {
            return;
        }
        int finalDeep = --deep;
        pluginManager.getPluginInstances().stream().filter(pluginOperatorOptions.wrapper(pluginFilter))
                .forEach(pi -> {
                    visitor.accept(pi);
                    pi.getPluginManager().createPluginVisitor(pluginOperatorOptions).every(pluginFilter, finalDeep, visitor);
                });
    }

    @Override
    public List<IPluginInstance> list(GAV gav, int deep) {
        return list(PluginOperatorHelper.createGavFilter(gav), deep);
    }

    @Override
    public List<IPluginInstance> list(Predicate<IPluginInstance> pluginFilter, int deep) {
        List<IPluginInstance> pis = new ArrayList<>();
        every(pluginFilter, deep, pis::add);
        return pis;
    }

    public IPluginInstance add(PluginParseInfo parseInfo){
        return null;
    }
}
