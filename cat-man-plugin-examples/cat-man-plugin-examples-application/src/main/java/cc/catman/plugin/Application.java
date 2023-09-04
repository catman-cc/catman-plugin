package cc.catman.plugin;

import cat.man.plugin.example.api.NameService;
import cc.catman.plugin.common.GAV;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.event.EventListenerBuilder;
import cc.catman.plugin.event.ObjectEventAck;
import cc.catman.plugin.event.extensionPoint.ExtensionPointInfoEvent;
import cc.catman.plugin.event.extensionPoint.WatchExtensionPointEventListener;
import cc.catman.plugin.handlers.repository.LocalRepositoryOption;
import cc.catman.plugin.handlers.repository.LocalRepositoryPluginParserInfoHandler;
import cc.catman.plugin.handlers.maven.MavenLikePluginStorageStrategy;
import cc.catman.plugin.handlers.maven.MavenMarketplacePluginParserInfoHandler;
import cc.catman.plugin.handlers.maven.MavenOptions;
import cc.catman.plugin.handlers.maven.NormalMavenLibsMarketplacePluginParserInfoHandler;
import cc.catman.plugin.operator.IPluginExtensionPointOperator;
import cc.catman.plugin.provider.DirectPluginDescribeProvider;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


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
//        DeveloperClassesFilePluginDescribeProvider provider =
//                DeveloperClassesFilePluginDescribeProvider.of("cat-man-plugin-examples/cat-man-plugin-examples-plugins/target/classes/");

        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        DirectPluginDescribeProvider directPluginDescribeProvider = new DirectPluginDescribeProvider(() -> Arrays.asList(
                PluginParseInfo.builder()
                        .group("cc.catman.plugin")
                        .name("cat-man-plugin-examples-plugins")
                        .version("1.0.8")
                        .build()

        ));

        pluginConfiguration.addPluginDescribeProvider(directPluginDescribeProvider);


        MavenOptions mavenOptions = MavenOptions.builder()
                .mavenExecuteFile("mvn")
                .globalSettingPath("/Users/jpanda/.m2/settings.xml")
                .userSettingPath("/Users/jpanda/work/codes/customer/cat-man/cat-man-plugin/cat-man-plugin-manager/src/test/resources/maven-user-settings.xml")
                .repoUrl("http://127.0.0.1:31081/repository/maven-snapshots/")
                .localRepositoryDirectory("/Users/jpanda/.m2/repository")
                .pluginRepositoryDirectory("/Users/jpanda/work/temp")
                .mavenHome("/Users/jpanda/.sdkman/candidates/maven/current/")
                .baseDir("/Users/jpanda/work/temp/cc/catman/plugin/cat-man-plugin-examples-plugins/")
                .debug(false)
                .build();

        LocalRepositoryOption localRepositoryOption = new LocalRepositoryOption();
        localRepositoryOption.setRepositoryDir(Paths.get("/Users/jpanda/work/temp"));
        localRepositoryOption.setPluginStorageStrategy(new MavenLikePluginStorageStrategy());


        pluginConfiguration.getParsingProcessProcessorConfiguration()
                .addHandler(new LocalRepositoryPluginParserInfoHandler(localRepositoryOption))
                .addHandler(new NormalMavenLibsMarketplacePluginParserInfoHandler(mavenOptions))
                .addHandler(new MavenMarketplacePluginParserInfoHandler(mavenOptions))
                ;

        RootPluginManager pm = RootPluginManager.from(pluginConfiguration);
        pm.start();

        IPluginExtensionPointOperator pluginExtensionPointOperator = pm.createPluginExtensionPointOperator();
        List<NameService> list = new ArrayList<>();
        // 如果使用这种直接访问ExtensionPoint的监听器,会在卸载插件时,造成内存泄漏
        WatchExtensionPointEventListener watchExtensionPointEventListener = new WatchExtensionPointEventListener() {
            @Override
            protected ObjectEventAck onStop(ExtensionPointInfoEvent event) {
                System.out.println(event.getExtensionPointInfo().getClassName() + "stop");
                for (NameService service : new ArrayList<>(list)) {
                    if (event.getExtensionPointInfo().getClazz().equals(service.getClass())){
                        list.remove(service);
                    }
                }
                return super.onStop(event);
            }
        };
        pluginConfiguration.addListener(EventListenerBuilder.wrapper(watchExtensionPointEventListener));


        list.addAll(pluginExtensionPointOperator.list(NameService.class, Optional.of(watchExtensionPointEventListener)));
        list.forEach(ns -> {
            System.out.println(ns.echo("===> [application start]"));
        });


//        pm.unInstall(GAV.builder().group("cc.catman.plugin")
//                .name("cat-man-plugin-examples-plugins")
//                .version("1.0.0").build());
//        // 无法获取插件实例对象了,但是已经被加载的实例依然可以使用,这是因为 对象->class->classloader
//        // 那如何动态移除list中的元素呢?
//        // 1. 订阅事件,订阅事件,相对来说比较容易实现
//
//        pluginConfiguration.removeListener(watchExtensionPointEventListener);
//        watchExtensionPointEventListener=null;
//
//        list.forEach(ns -> {
//            System.out.println(ns.echo("===> [application start]"));
//        });
//
//        List<IPluginInstance> s = pm.install(PluginParseInfo.builder()
//                .group("cc.catman.plugin")
//                .name("cat-man-plugin-examples-plugins")
//                .version("1.0.0")
//                .build());
//        s.forEach(IPluginInstance::start);
//
//        list.addAll(pluginExtensionPointOperator.list(NameService.class));
//        list.forEach(ns -> {
//            System.out.println(ns.echo("===> [application start]"));
//        });
    }
}
