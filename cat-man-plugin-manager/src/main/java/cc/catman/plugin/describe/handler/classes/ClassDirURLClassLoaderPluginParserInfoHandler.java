package cc.catman.plugin.describe.handler.classes;

import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.ClassDirPluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import cc.catman.plugin.describe.handler.AbstractURLClassLoaderPluginParserInfoHandler;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ClassDirURLClassLoaderPluginParserInfoHandler extends AbstractURLClassLoaderPluginParserInfoHandler {
    @Override
    protected List<String> getKinds() {
        return Collections.singletonList( EPluginKind.CLASSES_DIR.name());
    }

    @Override
    protected List<String> getSources() {
        return Collections.singletonList( EPluginSource.LOCAL.name());
    }

    @Override
    @SneakyThrows
    public List<PluginParseInfo> handler(PluginParseInfo parseInfo) {
        // 支持ant语法
        final ClassDirPluginParseInfo spd= ClassDirPluginParseInfo.class.isAssignableFrom(parseInfo.getClass())
                ?(ClassDirPluginParseInfo) parseInfo
                :parseInfo.decode(ClassDirPluginParseInfo.class);
        // 理论上这里的目录就是源码的根目录了,所以,可以直接从这里配置ClassLoader的根路径即可
        Path baseDir= spd.getResource().getFile().toPath().resolve(spd.getRelativePath()).normalize();
        spd.setBase(new FileSystemResource(baseDir));
        return build(spd, Collections.singletonList(baseDir.toFile().toURI().toURL()));
    }
}
