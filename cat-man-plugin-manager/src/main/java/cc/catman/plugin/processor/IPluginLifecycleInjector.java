package cc.catman.plugin.processor;

import cc.catman.plugin.runtime.IPluginManager;

public interface IPluginLifecycleInjector {
    IInjectPluginLifecycle inject(IParsingProcessProcessor processor, IPluginManager pluginManager);
}
