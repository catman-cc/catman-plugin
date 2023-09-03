package cc.catman.plugin.handlers;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.label.Label;
import cc.catman.plugin.core.label.Labels;
import cc.catman.plugin.core.label.metadata.EMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PluginParseInfoHelper {
    protected List<String> transferLabelNames=new ArrayList<>();

    public PluginParseInfoHelper() {
    }

    public PluginParseInfoHelper(List<String> transferLabelNames) {
        this.transferLabelNames = transferLabelNames;
    }
    public PluginParseInfo create(){
        return new PluginParseInfo();
    }
    public PluginParseInfo from(PluginParseInfo parseInfo){
        // 处理标签传递的基础逻辑.根据标签上的元数据,判断标签是否能够传递给衍生出来的PluginParseInfo
        PluginParseInfo pluginParseInfo=new PluginParseInfo();
        copyLabels(parseInfo,pluginParseInfo);
        return pluginParseInfo;
    }

    public void copyLabels(PluginParseInfo source,PluginParseInfo target){
        Labels ols = source.getLabels();
        Labels nls = target.getLabels();
        ols.getItems().forEach((key, label) -> {
            if (isTransfer(label)) {
                nls.add(label);
            }
        });
    }

    public boolean isTransfer(Label l){
       return transferLabelNames.contains(l.getName())||Optional.ofNullable(l.getLabels())
               .map(ls->
                       ls.find(EMetadata.TRANSFER.v())
                       .map(Label::isTrue)
                       .orElse(false))
               .orElse(false);
    }
}
