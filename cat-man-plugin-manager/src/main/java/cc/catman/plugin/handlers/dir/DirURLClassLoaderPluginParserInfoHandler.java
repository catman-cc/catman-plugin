package cc.catman.plugin.handlers.dir;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.handlers.AbstractPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 目录类型的插件信息,用于提供一组插件,该插件将会根据得到的目录信息,生成一组search插件.
 */
public class DirURLClassLoaderPluginParserInfoHandler extends AbstractPluginParserInfoHandler {
    @Override
    public List<String> lifeCycles() {
        // 只处理不明生命周期和分析阶段的信息
        return Arrays.asList(ELifeCycle.UNKNOWN.name(),ELifeCycle.ANALYZE.name());
    }

    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
        // 没有指定GAV,如果指定了GAV,那完全可以通过插件市场等其他方式来获取,而不是通过笨拙的查询
        // 有基础目录
        // 有指定的目录集
       return !parseInfo.hasGAV()
        &&Optional.ofNullable(parseInfo.getBaseDir()).isPresent()
        && Optional.ofNullable(parseInfo.getDynamicValues().get("dirs")).isPresent();
    }

    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        // 通过聚合后的资源访问器来迭代访问所有资源,获取插件描述文件,所以该插件的作用就是作为中转,生成一组SEARCH阶段的插件

        if ( Optional.ofNullable(parseInfo.getBaseDir()).isPresent()){
            DirPluginParseInfo pi = parseInfo.decode(DirPluginParseInfo.class);
            String baseDir=pi.getBaseDir();
            pi.getDirs().stream().map(dir->{
                if (Paths.get(dir).isAbsolute()){
                    return dir;
                }
                return Paths.get(baseDir,dir);
            }).forEach(dir->{
                PluginParseInfo next = processor.createNext(parseInfo);
                next.setBaseDir(dir.toString());
                next.setLifeCycle(ELifeCycle.SEARCH.name());
            });
            processor.finish(parseInfo);
        }
        return true;
    }
}
