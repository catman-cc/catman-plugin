package cc.catman.plugin.handlers.afterload;

import cc.catman.plugin.runtime.IPluginManager;

public interface IDependenciesLoadStrategyFactory {

    IDependenciesLoadStrategy create(IPluginManager pluginManager);
}
