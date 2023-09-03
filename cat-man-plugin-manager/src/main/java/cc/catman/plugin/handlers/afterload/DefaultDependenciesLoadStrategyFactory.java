package cc.catman.plugin.handlers.afterload;

import cc.catman.plugin.runtime.IPluginManager;

public class DefaultDependenciesLoadStrategyFactory implements IDependenciesLoadStrategyFactory{

    @Override
    public IDependenciesLoadStrategy create(IPluginManager pluginManager) {
        return new HierarchyDependenciesLoadStrategy();
    }
}
