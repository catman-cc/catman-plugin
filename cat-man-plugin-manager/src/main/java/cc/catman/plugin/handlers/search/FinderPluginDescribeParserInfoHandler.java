package cc.catman.plugin.handlers.search;

import cc.catman.plugin.common.Constants;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.enums.DescribeConstants;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.resources.CombineResourceBrowser;
import cc.catman.plugin.resources.CombineResourceVisitor;
import cc.catman.plugin.resources.ResourceVisitor;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class FinderPluginDescribeParserInfoHandler extends AbstractPluginParserInfoHandler {
    private CombineResourceBrowser resourceBrowser;
    private List<Supplier<FinderJob>> jobCreates;

    public FinderPluginDescribeParserInfoHandler(CombineResourceBrowser resourceBrowser) {
        this.resourceBrowser = resourceBrowser;
        this.jobCreates=createDefaultJobs();
    }
    public FinderPluginDescribeParserInfoHandler addJobCrate(Supplier<FinderJob> create){
        this.jobCreates.add(create);
        return this;
    }
    protected List<Supplier<FinderJob>> createDefaultJobs() {
        List<Supplier<FinderJob>> creates=new ArrayList<>();
        creates.add(()-> new FinderJob() {
            @Override
            protected boolean process(StandardPluginDescribe pluginDescribe, Resource resource) {
                return Optional.ofNullable((resource.getFilename()))
                        .map(fn->{
                            if (fn.startsWith(Constants.PLUGIN_DESCRIBE_FILE_NAME + ".")){
                                //  将描述文件传入配置,此时需要继续处理描述文件的解析操作

                                if ( !resource.isFile()){
                                    try {
                                        Path write = Files.write(Paths.get(pluginDescribe.getBaseDir(), resource.getFilename()), StreamUtils.copyToByteArray(resource.getInputStream()));
                                        log.debug("{} is not a file, so write the input stream of a to file {}.",resource.getFilename(),write);
                                        pluginDescribe.addOnUninstallFunction((pi) -> {
                                            if (write.toFile().delete()) {
                                                log.debug("delete the file: {}", write);
                                            }
                                        });
                                        pluginDescribe.setDescribeResource(new FileSystemResource(write));
                                        pluginDescribe.setRelativePath("../");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }else {
                                    try {
                                        log.debug("Find a plugin description file, the name is: {}",resource.getFile().toPath());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    pluginDescribe.setDescribeResource(resource);
                                }
                                pluginDescribe.getLabels()
                                        .add(EDescribeLabel.EXCLUSIVE_PARSER.label(), DescribeConstants.NEED_PARSER_PLUGIN_DESCRIBE_FILE);

                                return true;
                            }
                            return false;
                        })
                        .orElse(false);
            }
        });
        creates.add(()->new FinderJob() {
            @Override
            protected boolean process(StandardPluginDescribe pluginDescribe, Resource resource) {
                return Optional.ofNullable((resource.getFilename()))
                        .map(fn->{
                            if (fn.startsWith(Constants.PLUGIN_MAVEN_NORMAL_DEPENDENCIES_FILE_NAME)){
                                // 将描述文件传入配置
                                // 加入一个需要解析MAVEN文件的标签
                                log.debug("Find a normal dependency file, the name is: {}",resource.getFilename());
                                pluginDescribe.getLabels()
                                        .add(EDescribeLabel.EXCLUSIVE_PARSER.label(), DescribeConstants.NEED_PARSER_MAVEN_NORMAL_LIBS_FILE);

                                pluginDescribe.setNormalDependencyType("MAVEN");
                                pluginDescribe.setNormalDependencyLibrariesDescrbieResource(resource);
                                return true;
                            }
                            return false;
                        })
                        .orElse(false);
            }
        });

        return creates;
    }

    @Override
    public List<String> lifeCycles() {
        return Collections.singletonList(ELifeCycle.SEARCH.name());
    }

    @Override
    public boolean doSupport(PluginParseInfo standardPluginDescribe) {
        // 没有描述文件,但是提供了基础目录,这就意味着可以通过扫描的方式来处理
        return Optional.ofNullable(standardPluginDescribe.getBaseDir()).isPresent();
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        List<FinderJob> jobs = createDefaultJobs().stream().map(Supplier::get).collect(Collectors.toList());
        CombineResourceVisitor resourceVisitor=new CombineResourceVisitor(resourceBrowser,new ResourceVisitor() {
            @Override
            public boolean visitor(Resource resource) {

                jobs.forEach(job->{
                    job.exec(parseInfo,resource);
                });
                return jobs.stream().anyMatch(FinderJob::notDone);
            }
        });
        resourceVisitor.visitor(new FileSystemResource(parseInfo.getBaseDir()));
        processor.next(parseInfo);
        return true;
    }
}
