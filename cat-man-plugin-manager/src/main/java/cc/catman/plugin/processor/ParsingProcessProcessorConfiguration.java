package cc.catman.plugin.processor;

import cc.catman.plugin.core.label.LabelsAnnotationHelper;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.handlers.PluginParseErrorHandler;
import cc.catman.plugin.handlers.PluginParseInfoHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParsingProcessProcessorConfiguration {
    @Builder.Default
    protected boolean historyLabel = false;
    @Builder.Default
    protected PluginParseErrorHandler errorHandler= (exception, processor) -> false;
    @Builder.Default
    protected IdGenerator idGenerator = new AlternativeJdkIdGenerator();

    @Builder.Default
    protected PluginParseInfoHelper pluginParseInfoHelper=new PluginParseInfoHelper(Arrays.stream(EDescribeLabel.values()).map(EDescribeLabel::label).collect(Collectors.toList()));

    @Builder.Default
    protected List<String> lifeCycles = new ArrayList<>(
            Arrays
                    .stream(ELifeCycle.values())
                    .map(ELifeCycle::name)
                    .collect(Collectors.toList())
    );

    @Builder.Default
    protected List<IPluginParserInfoHandler> handlers = new ArrayList<>();

    public ParsingProcessProcessorConfiguration addHandler(IPluginParserInfoHandler handler) {
        this.handlers.add(LabelsAnnotationHelper.handler(handler));
        return this;
    }

    public ParsingProcessProcessorConfiguration addFirst(IPluginParserInfoHandler handler) {
        this.handlers.add(0, handler);
        return this;
    }
    public ParsingProcessProcessorConfiguration addLast(IPluginParserInfoHandler handler) {
        this.handlers.add(this.handlers.size(), handler);
        return this;
    }
    public ParsingProcessProcessorConfiguration remove(Predicate<IPluginParserInfoHandler> test) {
        this.handlers.removeIf(test);
        return this;
    }

    public ParsingProcessProcessorConfiguration replaceFirst(Predicate<IPluginParserInfoHandler> test, IPluginParserInfoHandler handler) {
        this.handlers.stream().filter(test).findFirst().ifPresent(h -> {
            int i = this.handlers.indexOf(h);
            if (i != -1) {
                this.handlers.remove(i);
                this.handlers.add(i, handler);
            }
        });
        return this;
    }

}
