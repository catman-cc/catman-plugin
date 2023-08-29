package cc.catman.plugin.describe.parser;

import cc.catman.plugin.common.Constants;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.enmu.EPluginParserStatus;
import cc.catman.plugin.describe.resources.IResourceBrowser;
import cc.catman.plugin.describe.resources.ResourceVisitor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 其实类名称应该是FindPluginDescribeParserPluginDescribeParser,他的作用是查找插件的描述文件,但是很明显两个单词重复了,所以简写了.
 * <p>
 * 应该怎么查找描述文件呢? 需要查找的描述文件,可能包含:
 * - 在目录下查找文件
 * - 在zip压缩包中查找文件
 * - 在jar包中查找文件
 * - 在tar包中查找文件
 * ...
 * 然后就是要查找什么样的文件?
 * 所以查找文件需要两个东西:
 * - 迭代器,针对各种文件的迭代器,目录,jar包等等
 * - 验证器,消费迭代器获取的所有文件,然后交给验证验证是否符合要求
 * 符合验证器的要求之后.是否要中断查找操作,感觉这就是Files.walk方法了.只不过更细致一些,允许进入到某些文件中读取文件.
 * 所以迭代器交给验证器的文件可以获取文件内容.
 * <p>
 * 是否需要解压操作?
 * <p>
 * 貌似不需要,除了jar包之外,类似于zip,tar之类的压缩文件,应该是谁下载谁解压,不应该交给迭代器,迭代器只负责访问?
 */
public class FinderPluginDescribeParser implements IPluginDescribeParser {

    private IResourceBrowser resourceBrowser;

    private List<FinderJob> jobs;

    public FinderPluginDescribeParser(IResourceBrowser resourceBrowser) {
        this.resourceBrowser = resourceBrowser;
        this.jobs = createDefaultJobs();
    }

    private List<FinderJob> createDefaultJobs() {
        ArrayList<FinderJob> js=new ArrayList<>();
        js.add(new FinderJob() {
            @Override
            protected boolean process(StandardPluginDescribe pluginDescribe, Resource resource) {
                return Optional.ofNullable((resource.getFilename()))
                        .map(fn->{
                            if (fn.startsWith(Constants.PLUGIN_DESCRIBE_FILE_NAME+".")){
                                // 将描述文件传入配置
                                pluginDescribe.setDescribeResource(resource);
                                return true;
                            }
                            return false;
                        })
                        .orElse(false);
            }
        });
        js.add(new FinderJob() {
            @Override
            protected boolean process(StandardPluginDescribe pluginDescribe, Resource resource) {
                return Optional.ofNullable((resource.getFilename()))
                        .map(fn->{
                            if (fn.startsWith(Constants.PLUGIN_MAVEN_NORMAL_DEPENDENCIES_FILE_NAME+".")){
                                // 将描述文件传入配置
                                pluginDescribe.setNormalDependencyType("MAVEN");
                                pluginDescribe.setNormalDependencyLibrariesResource(resource);
                                return true;
                            }
                            return false;
                        })
                        .orElse(false);
            }
        });
        return js;
    }

    @Override
    public boolean supports(StandardPluginDescribe standardPluginDescribe) {
        return !Optional.ofNullable(standardPluginDescribe.getDescribeResource()).isPresent()
               && Optional.ofNullable(standardPluginDescribe.getBaseDir()).isPresent();
    }

    @Override
    public PluginParseInfo wrapper(StandardPluginDescribe standardPluginDescribe) {
        // 获取所有的描述文件查找工具,进行查找操作
        AtomicReference<Resource> pluginDescribeFile = new AtomicReference<>();
        // 感觉这个阶段不要浪费,如果有可能的话,直接把第三方依赖文件也给扫描出来,填充好,免得重复处理了
        // 类描述文件,也是具备通用的格式

        // TODO finder可以同时接受多个查找任务,每个查找任务都在visitor里面执行,任务执行完毕之后,标记为done
        // 当所有的任务都被标记为done的时候,visitor返回停止查找标签.
        // 每个内容都可以访问到当前StandardPluginDescribe,便于往里面赋值,或者修改值.
        // 甚至,因为有些文件的格式是固定的,比如普通第三方依赖包的描述文件,所以在task里面就可以完成解析,然后赋值两个操作.
        //
        PluginParseInfo parseInfo = PluginParseInfo.builder()
                .status(EPluginParserStatus.RE_PARSE) // 这里一定要设置为RE_PARSE
                .baseDir(standardPluginDescribe.getBaseDir())
                .build();
        resourceBrowser.browser(new FileSystemResource(standardPluginDescribe.getBaseDir()), new ResourceVisitor() {
            @Override
            public boolean visitor(Resource resource) {
                jobs.forEach(job->{
                    job.exec(parseInfo,resource);
                });
                return jobs.stream().anyMatch(FinderJob::notDone);
            }
        });
        if (parseInfo.getDescribeResource() != null) {
            return parseInfo;

        }
        // 没有找到类描述文件,这就表示这不是一个合格的插件,记录异常,抛出事件,上层会自动处理null
        return null;
    }

    @Override
    public <T extends StandardPluginDescribe> T decode(PluginParseInfo parseInfo, Class<T> clazz) {
        // 该方法永远不会被执行
        throw new UnsupportedOperationException(
                "The decode method should never be called. Please check whether the" +
                " [PluginParserContext] is missing the necessary parser so that it cannot convert " +
                "the describe file of the plugin into the necessary object.");
    }
}
