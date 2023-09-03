package cc.catman.plugin.handlers.classes;

import cc.catman.plugin.core.describe.PluginParseInfo;
import cc.catman.plugin.enums.EDescribeLabel;
import cc.catman.plugin.enums.ExclusiveParserValue;
import cc.catman.plugin.handlers.AbstractURLClassLoaderPluginParserInfoHandler;
import cc.catman.plugin.enums.ELifeCycle;
import cc.catman.plugin.processor.IParsingProcessProcessor;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 用户提供了包含GAV坐标的源码目录,
 */
public class ClassDirURLClassLoaderPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {
    @Override
    public List<String> lifeCycles() {
        return Arrays.asList(ELifeCycle.LOAD.name());
    }

    @Override
    protected List<String> withoutExclusiveParser() {
        return Collections.singletonList(ExclusiveParserValue.DEVELOPER_CLASS_DIR.name());
    }

    @Override
    public boolean doSupport(PluginParseInfo parseInfo) {
        // 这里需要包含 GAV坐标
        // 类描述文件,基础工作目录
        return parseInfo.hasGAV()
                && Optional.ofNullable(parseInfo.getBaseDir()).isPresent()
                && Optional.ofNullable(parseInfo.getDescribeResource()).isPresent();
    }

    @SneakyThrows
    @Override
    public boolean handler(IParsingProcessProcessor processor, PluginParseInfo parseInfo) {
        // 此处直接就是一个插件
        parseInfo.getLabels().rm(EDescribeLabel.EXCLUSIVE_PARSER.derive(ELifeCycle.LOAD.name()),ExclusiveParserValue.DEVELOPER_CLASS_DIR.name());
        Path baseDir= parseInfo.getDescribeResource().getFile().toPath().resolve(parseInfo.getRelativePath()).normalize();
        processor.registryPluginInstance(parseInfo);
        build(processor,parseInfo, Collections.singletonList(baseDir.toFile().toURI().toURL()));
        return false;
    }
}
