package cc.catman.plugin.processor;

import cc.catman.plugin.handlers.IPluginParserInfoHandler;

public interface IInjectPluginLifecycle {
    void injectLifecycle();

    IPluginParserInfoHandler providerHandler();

}
