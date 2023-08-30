//package cc.catman.plugin;
//
//import cc.catman.plugin.event.*;
//import cc.catman.plugin.event.extensionPoint.ExtensionPointEvent;
//import cc.catman.plugin.event.extensionPoint.ExtensionPointEventInfoName;
//import cc.catman.plugin.event.extensionPoint.ExtensionPointEventName;
//import cc.catman.plugin.event.extensionPoint.ExtensionPointInfoEvent;
//import cc.catman.plugin.provider.LocalFileSystemPluginDescribeProvider;
//import junit.framework.TestCase;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.stream.Collectors;
//
//public class RootPluginManagerTest extends TestCase {
//
//    public void testFrom() {
//        LocalFileSystemPluginDescribeProvider localFileSystemPluginDescribeProvider = LocalFileSystemPluginDescribeProvider
//                .builder()
//                .dirs(Collections.singletonList(System.getProperty("user.dir")))
//                .build();
//        PluginConfiguration pluginConfiguration = new PluginConfiguration();
//        pluginConfiguration.addPluginDescribeProvider(localFileSystemPluginDescribeProvider);
//
//        pluginConfiguration.getEventBus().addEventPublishers(
//                new DefaultEventPublisher<>(new DefaultIEventContext<ExtensionPointEvent, ObjectEventAck>()),
//                new DefaultEventPublisher<>(new DefaultIEventContext<ExtensionPointInfoEvent, ObjectEventAck>())
//        );
//        pluginConfiguration.getEventBus().addListener(
//                new AbstractEventListener<ExtensionPointEvent, ObjectEventAck>(Arrays.stream(ExtensionPointEventName.values()).map(Enum::name).collect(Collectors.toList())) {
//                    @Override
//                    public ObjectEventAck handler(ExtensionPointEvent event) {
//                        return null;
//                    }
//                });
//
//        RootPluginManager pm = RootPluginManager.from(pluginConfiguration);
//        pm.start();
//    }
//}