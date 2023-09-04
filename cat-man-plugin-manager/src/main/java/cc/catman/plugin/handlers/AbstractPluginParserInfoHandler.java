package cc.catman.plugin.handlers;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.label.ILabelAbility;
import cc.catman.plugin.core.label.Labels;
import cc.catman.plugin.core.label.filter.AndLabelFilter;
import cc.catman.plugin.core.label.filter.GroupLabelFilter;
import cc.catman.plugin.enums.EDescribeLabel;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

public abstract class AbstractPluginParserInfoHandler implements IPluginParserInfoHandler {
    @Getter
    protected Labels labels;

    protected GroupLabelFilter filter=new AndLabelFilter();

    public AbstractPluginParserInfoHandler() {
        this.labels=Labels.empty();
    }

    public boolean support(PluginParseInfo parseInfo) {
        return filter(parseInfo)&&
               parseInfo.getLabels().notExistLabelOrLabelHasAnyValue(EDescribeLabel.EXCLUSIVE_PARSER.derive(parseInfo.getLifeCycle()),withoutExclusiveParser())
               &&doSupport(parseInfo);
    }

    protected  boolean doSupport(PluginParseInfo parseInfo){
        return true;
    }

    protected List<String> withoutExclusiveParser(){
        return Collections.emptyList();
    }

    @Override
    public boolean filter(ILabelAbility l) {
        return filter.filter(l);
    }
}
