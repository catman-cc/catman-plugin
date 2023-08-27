package cc.catman.plugin;

import cat.man.plugin.example.api.NameService;
import cc.catman.plugin.event.DefaultEventPublisher;
import cc.catman.plugin.event.DefaultIEventContext;
import cc.catman.plugin.event.ObjectEventAck;
import cc.catman.plugin.event.extensionPoint.ExtensionPointEvent;
import cc.catman.plugin.event.extensionPoint.ExtensionPointInfoEvent;
import cc.catman.plugin.operator.IPluginExtensionPointOperator;
import cc.catman.plugin.provider.DeveloperClassesFilePluginDescribeProvider;


public class Application {
    public static void main(String[] args) {
        // 从当前工作空间下查找类描述文件.
//        LocalFileSystemPluginDescribeProvider provider = LocalFileSystemPluginDescribeProvider
//                .builder()
//                .dirs(Collections.singletonList(System.getProperty("user.dir")))
//                .pluginDescFileNamesPatterns(Arrays.asList(
//                        "cat-man-plugin-examples/cat-man-plugin-examples-plugins/target/classes/cat-man-plugin.*"
//                ))
//                .build();
        DeveloperClassesFilePluginDescribeProvider provider=
                DeveloperClassesFilePluginDescribeProvider.of("cat-man-plugin-examples/cat-man-plugin-examples-plugins/target/classes/");

        PluginConfiguration pluginConfiguration = new PluginConfiguration();

        pluginConfiguration.addPluginDescribeProvider(provider);

        pluginConfiguration.getEventBus().addEventPublishers(
                new DefaultEventPublisher<>(new DefaultIEventContext<ExtensionPointEvent, ObjectEventAck>()),
                new DefaultEventPublisher<>(new DefaultIEventContext<ExtensionPointInfoEvent, ObjectEventAck>())
        );
        RootPluginManager pm = RootPluginManager.from(pluginConfiguration);
        pm.start();

        IPluginExtensionPointOperator pluginExtensionPointOperator = pm.createPluginExtensionPointOperator();
        pluginExtensionPointOperator.list(NameService.class).forEach(ns->{
            System.out.println( ns.echo("===> [application start]"));
        });
    }
}
