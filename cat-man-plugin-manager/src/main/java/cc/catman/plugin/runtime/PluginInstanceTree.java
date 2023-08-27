package cc.catman.plugin.runtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PluginInstanceTree {

    protected IPluginInstance node;

    protected List<PluginInstanceTree> tree;
}
