package cc.catman.plugin.operator;

import cc.catman.plugin.common.GAV;
import cc.catman.plugin.event.extensionPoint.WatchExtensionPointEventListener;
import cc.catman.plugin.runtime.IPluginInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DefaultPluginExtensionPointOperator implements IPluginExtensionPointOperator {

    protected IPluginOperator pluginVisitor;

    public DefaultPluginExtensionPointOperator(IPluginOperator pluginVisitor) {
        this.pluginVisitor = pluginVisitor;
    }

    @Override
    public <T> List<T> list(Class<T> type, Optional<WatchExtensionPointEventListener> watch) {
        List<T> objs=new ArrayList<>();
        pluginVisitor.every(p->{
            IExtensionPointOperator ev = p.getExtensionPointManager().createIExtensionPointVisitor();
            objs.addAll(ev.list(type, watch));
        });
        return objs;
    }

    @Override
    public <T> List<T> list(Class<T> type) {
       return list(type,Optional.empty());
    }

    @Override
    public <T> List<T> list(Class<T> type, GAV gav) {
        List<T> objs=new ArrayList<>();
        pluginVisitor.every(PluginOperatorHelper.createGavFilter(gav), p->{
            IExtensionPointOperator ev = p.getExtensionPointManager().createIExtensionPointVisitor();
            objs.addAll(ev.list(type));
        });
        return objs;
    }

    @Override
    public <T> List<T> list(Class<T> type, GAV gav, int deep) {
        List<T> objs=new ArrayList<>();
        pluginVisitor.every(PluginOperatorHelper.createGavFilter(gav),deep, p->{
            IExtensionPointOperator ev = p.getExtensionPointManager().createIExtensionPointVisitor();
            objs.addAll(ev.list(type));
        });
        return objs;
    }

    @Override
    public <T> List<T> list(Class<T> type, Predicate<IPluginInstance> pluginFilter, int deep) {
        return null;
    }

    @Override
    public <T> List<T> list(Class<T> type, Predicate<IPluginInstance> pluginFilter, int deep, WatchExtensionPointEventListener watchExtensionPointEventListener) {
        return null;
    }
}
