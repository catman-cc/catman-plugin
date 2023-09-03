package cc.catman.plugin.core.label;

import cc.catman.plugin.core.label.metadata.EMetadata;

import java.util.Optional;

public class MetadataLabels {

    public static Label transfer(Label label){
        if (!Optional.ofNullable(label.getLabels()).isPresent()){
            label.setLabels(Labels.empty());
        }
        Labels labels = label.labels();
        labels.replace(Label.create(EMetadata.TRANSFER.v(),true));
        return label;
    }
}
