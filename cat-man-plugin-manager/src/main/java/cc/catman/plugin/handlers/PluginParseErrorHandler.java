package cc.catman.plugin.handlers;

import cc.catman.plugin.processor.IParsingProcessProcessor;

@FunctionalInterface
public interface PluginParseErrorHandler {
    boolean handle(RuntimeException exception, IParsingProcessProcessor processor);
}
