package cc.catman.plugin.extensionPoint.finder.scan;

import cc.catman.plugin.common.FileUtils;
import cc.catman.plugin.core.describe.StandardPluginDescribe;
import cc.catman.plugin.extensionPoint.ExtensionPointInfo;
import cc.catman.plugin.extensionPoint.finder.IExtensionPointFinder;
import cc.catman.plugin.runtime.IPluginInstance;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 为了加载效率,所有的类扫描操作,都应该统一到依次扫描过程中
 */
public class ClassScanExtensionPointFinder implements IExtensionPointFinder {
    protected IPluginInstance pluginInstance;
    protected List<IExtensionPointProcessor> extensionPointProcessors;

    public ClassScanExtensionPointFinder(IPluginInstance pluginInstance, IExtensionPointProcessor... extensionPointProcessors) {
        this.pluginInstance = pluginInstance;
        this.extensionPointProcessors=Arrays.asList(extensionPointProcessors);
    }

    @SneakyThrows
    protected List<String> findClassNames(){
        Resource resource=pluginInstance.getPluginParseInfo().getDescribeResource();
        File dir = resource.getFile();
        Path dirPath = dir.toPath();
        return FileUtils.deepFindFilesHandler(
                resource
                , f -> f.isDirectory() || f.getName().endsWith(".class")
                , file -> {
                    String className = dirPath.relativize(file.toPath()).toString()
                            .replaceAll("(\\/|\\\\)", ".");
                    return className.substring(0, className.length() - ".class".length());

                }
        );
    }
    @Override
    public Stream<ExtensionPointInfo> find(StandardPluginDescribe standardPluginDescribe) {
        return findClassNames().stream()
                .map(cn->{
                   ExtensionPointInfo extensionPointInfo= ExtensionPointInfo.builder()
                            .className(cn)
                            .build();
                    try {
                        extensionPointInfo.setClazz(pluginInstance.getClassLoader().loadClass(cn));
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                    return extensionPointInfo;
                })
                .filter(Objects::nonNull)
                .peek(epi-> extensionPointProcessors.forEach(p->{
                    p.handle(epi);
                })).filter(ExtensionPointInfo::isValid);
    }

    public ClassScanExtensionPointFinder addProcessor(IExtensionPointProcessor processor){
        this.extensionPointProcessors.add(processor);
        return this;
    }
}
