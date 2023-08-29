package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.event.parser.EPluginParseEventName;
import cc.catman.plugin.event.parser.PluginParseEvent;
import cc.catman.plugin.runtime.IPluginConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DefaultPluginParserInfoHandlerContext implements IPluginParserInfoHandlerContext {
    protected IPluginConfiguration pluginConfiguration;
    protected List<IPluginParserInfoHandler> handlers = createHandlers();

    public DefaultPluginParserInfoHandlerContext(IPluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    private List<IPluginParserInfoHandler> createHandlers() {
        return new ArrayList<>();
    }

    @Override
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        pluginConfiguration.publish(PluginParseEvent.builder()
                .eventName(EPluginParseEventName.START.name())
                .parseInfo(parseInfo)
                .build());

        ArrayList<PluginParseInfo> successParsed = new ArrayList<>();
        Optional<IPluginParserInfoHandler> first = this.handlers.stream().filter(h -> h.support(parseInfo)).findFirst();

        if (!first.isPresent()) {
            pluginConfiguration.publish(PluginParseEvent.builder()
                    .eventName(EPluginParseEventName.WARN_CAN_NOT_FOUND_HANDLE.name())
                    .parseInfo(parseInfo)
                    .build()
            );
            // TODO 没有能够处理该插件的处理器,需要抛出事件
            return successParsed;
        }
        List<PluginParseInfo> parsed = first.get().handler(parseInfo);
        pluginConfiguration.publish(PluginParseEvent.builder()
                .eventName(EPluginParseEventName.HANDLED.name())
                .parseInfo(parseInfo)
                .parsedList(parsed)
                .build());

        parsed
                .forEach(ppi -> {
                    switch (ppi.getStatus()) {
                        case RE_PARSE:{
                            // 当前插件在处理之后,生成了全新的插件信息,需要重新解析,并输出新的描述文件
                            pluginConfiguration.getPluginParserContext().parser(ppi)
                                    .forEach(pi->{
                                       successParsed.addAll(handler(pi));
                                    });
                        }
                        case WAIT_PARSE: {
                            // 递归进行解析操作
                            successParsed.addAll(handler(ppi));
                        }
                        case COMPLETE: {
                            // 插件完成解析
                            // 插件解析成功,无需继续解析,进入下一阶段
                            successParsed.add(ppi);
                            return;
                        }
                        case FAIL: {
                            // TODO 解析失败,意味着,意味着拥有错误的配置信息或者其他异常,需要推送事件
                        }
                    }
                });
        return successParsed;
    }

    @Override
    public List<IPluginParserInfoHandler> getHandlers() {
        return this.handlers;
    }

    @Override
    public IPluginParserInfoHandlerContext addHandler(IPluginParserInfoHandler handler) {
        this.handlers.add(handler);
        return this;
    }

    @Override
    public IPluginParserInfoHandlerContext addFirst(IPluginParserInfoHandler handler) {
        this.handlers.add(0, handler);
        return this;
    }

    @Override
    public IPluginParserInfoHandlerContext remove(Predicate<IPluginParserInfoHandler> test) {
        this.handlers.removeIf(test);
        return this;
    }

    @Override
    public boolean replaceFirst(Predicate<IPluginParserInfoHandler> test, IPluginParserInfoHandler handler) {
        Optional<IPluginParserInfoHandler> first = this.handlers.stream().filter(test).findFirst();
        return first.map(h -> {
            int i = this.handlers.indexOf(h);
            if (i != -1) {
                this.handlers.remove(i);
                this.handlers.add(i, handler);
                return true;
            }
            return false;
        }).orElse(false);
    }
}
