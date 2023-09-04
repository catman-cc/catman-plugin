package cc.catman.plugin.handlers.repository;

import cc.catman.plugin.common.Constants;
import cc.catman.plugin.common.GAV;
import cc.catman.plugin.common.JacksonSerialization;
import cc.catman.plugin.common.Mapper;
import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.handlers.IPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class LocalRepositoryPluginParserInfoHandler extends AbstractPluginParserInfoHandler {
    protected LocalRepositoryOption option;

    protected ObjectMapper objectMapper= JacksonSerialization.AddResourceSerializatin(new JsonMapper())
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false)
            .configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,false)
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false)
            ;

    public LocalRepositoryPluginParserInfoHandler(LocalRepositoryOption option) {
        this.option = option;
    }

    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
        if (ELifeCycle.PRE_LOAD.name().equals(parseInfo.getLifeCycle())) {
            return Optional.ofNullable(parseInfo.getBaseDir()).isPresent()
                   &&parseInfo.getLabels().noExist(EDescribeLabel.LOCAL_CACHED.label()) // 避免被多次重复缓存
                   && Optional.ofNullable(parseInfo.getDescribeResource()).isPresent()
                   && parseInfo.hasGAV();
        }

        // 如果一个插件没有描述文件,没有工作目录,那很明显,插件需要由插件市场来处理,那就尝试从maven仓库下载
        return !StringUtils.hasText(parseInfo.getBaseDir())
               && parseInfo.getDescribeResource() == null
               && parseInfo.hasGAV();
    }

    @Override
    public List<String> lifeCycles() {
        return Arrays.asList(ELifeCycle.ACHIEVE.name(), ELifeCycle.UNKNOWN.name(), ELifeCycle.PRE_LOAD.name());
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        if (ELifeCycle.PRE_LOAD.name().equals(parseInfo.getLifeCycle())) {
            return saveToLocal(processor, parseInfo);
        }
        try {
            resolveCache(processor,parseInfo);
            return !ELifeCycle.FINISHED.name().equals(parseInfo.getLifeCycle())&&!ELifeCycle.PRE_LOAD.name().equals(parseInfo.getLifeCycle());
            // 获取插件的缓存信息,
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PluginParseInfo resolveCache(IParsingProcessProcessor processor,PluginParseInfo parseInfo) throws IOException {
        Path pluginDir = buildPluginDir(parseInfo);
        // 然后尝试从该目录下加载插件
//        parseInfo.setBaseDir(pluginDir.toString());
        // 读取缓存数据
        File cache = pluginDir.resolve(Constants.LOCAL_CACHE_FILE_NAME).toFile();
        if (!cache.exists()){
            // 没有缓存文件,走正常流程
            processor.next(parseInfo);
            return parseInfo;
        }
        PluginParseInfo cachePluginInfo = objectMapper.readValue(cache, PluginParseInfo.class);
        if (cachePluginInfo.getDynamicValues().containsKey("redirect")){
            PluginParseInfo redirect = Mapper.get().map(cachePluginInfo.getDynamicValues().get("redirect"), PluginParseInfo.class);
            processor.finish(parseInfo);
            return resolveCache(processor,redirect);
        }
        log.debug("[{}] match local cache,dir:{}",cachePluginInfo.toGAV().toString(),cachePluginInfo.getBaseDir());
        cachePluginInfo.getLabels().add(EDescribeLabel.LOAD_FROM_CACHE.label(),System.currentTimeMillis());
        processor.finish(parseInfo);
        processor.next(cachePluginInfo);
        return cachePluginInfo;
    }

    private boolean saveToLocal(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        // 判断基本信息是否符合要求
        try {
            Path pluginDir = buildPluginDir(parseInfo);
            Path workDIr= Paths.get( parseInfo.getBaseDir());
            if (!pluginDir.equals(workDIr)){
                parseInfo.setDescribeResource(resetResource(pluginDir.toString(),parseInfo.getBaseDir(),parseInfo.getDescribeResource()));
                parseInfo.setNormalDependencyLibraries(parseInfo.getNormalDependencyLibraries().stream().map(resource -> {
                    try {
                        return resetResource(pluginDir.toString(),parseInfo.getBaseDir(),resource);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));
                parseInfo
                        .setNormalDependencyLibrariesDescrbieResource(resetResource(pluginDir.toString(),parseInfo.getBaseDir(),parseInfo.getNormalDependencyLibrariesDescrbieResource()));
                parseInfo.setBaseDir(pluginDir.toString());
                // 复制资源
                FileSystemUtils.copyRecursively(workDIr,pluginDir);
                if (option.isDeleteOther()){
                    FileSystemUtils.deleteRecursively(workDIr);
                }
            }

            File cache = pluginDir.resolve(Constants.LOCAL_CACHE_FILE_NAME).toFile();
            if (cache.exists()){
                cache.delete();
            }
            cache.createNewFile();

            parseInfo.getLabels().add(EDescribeLabel.LOCAL_CACHED.label(), System.currentTimeMillis());
            objectMapper.writeValue(new FileWriter(cache),parseInfo);

            handlerFrom(parseInfo);

            processor.next(parseInfo);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public Path buildPluginDir(PluginParseInfo parseInfo) throws IOException {
       return option.getPluginStorageStrategy().covert(option.getRepositoryDir(), parseInfo, true);
    }
    public void copyJarFile(Resource resource) throws IOException {
        URL url = ResourceUtils.extractJarFileURL(resource.getURL());
    }

    public void handlerFrom(StandardPluginDescribe pd){
        Optional.ofNullable(pd.getFrom())
                .ifPresent(f->{
                    if (pd.toGAV().equals(f.toGAV())){
                        return;
                    }
                    PluginParseInfo parseInfo=new PluginParseInfo();
                    parseInfo.setGroup(f.getGroup());
                    parseInfo.setName(f.getName());
                    parseInfo.setVersion(f.getVersion());
                    try {
                        Path pluginDir = buildPluginDir(parseInfo);
                        File cache = pluginDir.resolve(Constants.LOCAL_CACHE_FILE_NAME).toFile();
                        if (cache.exists()){
                            cache.delete();
                        }
                        cache.createNewFile();
                        Map<String, Object> dynamicValues = parseInfo.getDynamicValues();
                        dynamicValues.put("redirect", pd.toGAV());
                        objectMapper.writeValue(cache,parseInfo);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    handlerFrom(f);
                });
    }
    public Resource resetResource(String pluginDir,String baseDir,Resource resource) throws IOException {
        if (resource.isFile()){
            Path dp = resource.getFile().toPath();
            if (dp.startsWith(baseDir)){
                Path relativize = Paths.get(baseDir).relativize(dp);
                return new FileSystemResource(Paths.get(pluginDir).resolve(relativize).normalize());
            }
        }else if (ResourceUtils.isJarURL(resource.getURL())) {
            String innerFileUrl = resource.getURL().toString();
            String replace = innerFileUrl.replaceFirst(baseDir, pluginDir);
            return new UrlResource(replace);
        }
        return resource;
    }
}
