package cc.catman.plugin.processor;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.runtime.IPluginManager;

import java.util.ArrayList;
import java.util.List;

public class DefaultParsingProcessProcessorFactory implements IParsingProcessProcessorFactory{

    private ParsingProcessProcessorConfiguration parsingProcessProcessorConfiguration;

    public DefaultParsingProcessProcessorFactory(ParsingProcessProcessorConfiguration parsingProcessProcessorConfiguration) {
        this.parsingProcessProcessorConfiguration = parsingProcessProcessorConfiguration;
    }

    protected List<IPluginLifecycleInjector> injectPluginLifecycles = createLifeCycles();

    private List<IPluginLifecycleInjector> createLifeCycles() {
        return new ArrayList<>();
    }

    protected List<IPluginParserInfoHandler> handlers = createHandlers();
    private List<IPluginParserInfoHandler> createHandlers() {
        return new ArrayList<>();
    }
    @Override
    public List<IPluginParserInfoHandler> getHandlers() {
        return this.handlers;
    }

    @Override
    public IParsingProcessProcessorFactory registerLifecycleInjector(IPluginLifecycleInjector lifecycleInjector) {
        this.injectPluginLifecycles.add(lifecycleInjector);
        return this;
    }

    @Override
    public DefaultParsingProcessProcessor create(List<PluginParseInfo> parseInfos, IPluginManager ownerPluginManager){
        return DefaultParsingProcessProcessor
                .builder()
                .currentRound(new ArrayList<>(parseInfos))
                .historyLabel(parsingProcessProcessorConfiguration.historyLabel)
                .parserInfoHandlerList(parsingProcessProcessorConfiguration.getHandlers())
                .idGenerator(parsingProcessProcessorConfiguration.idGenerator)
                .pluginParseInfoHelper(parsingProcessProcessorConfiguration.pluginParseInfoHelper)
                .lifeCycles(parsingProcessProcessorConfiguration.lifeCycles)
                .lifecycleInjectors(injectPluginLifecycles)
                .ownerPluginManager(ownerPluginManager)
                .build().init();
    }
}
