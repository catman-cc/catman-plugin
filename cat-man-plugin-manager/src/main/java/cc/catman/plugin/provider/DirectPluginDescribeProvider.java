package cc.catman.plugin.provider;

import cc.catman.plugin.core.describe.PluginParseInfo;

import java.util.List;
import java.util.function.Supplier;

public class DirectPluginDescribeProvider extends AbstractPluginDescribeProvider{

    protected Supplier<List<PluginParseInfo>> PluginDescribeSupplier;

    public DirectPluginDescribeProvider(Supplier<List<PluginParseInfo>> pluginDescribeSupplier) {
        PluginDescribeSupplier = pluginDescribeSupplier;
    }

    @Override
    public List<PluginParseInfo> provider() {
        return PluginDescribeSupplier.get();
    }
}
