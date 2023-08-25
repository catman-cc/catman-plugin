package cc.catman.plugin.describe.handler;

import cc.catman.plugin.describe.PluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.ClassDirPluginDescribe;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import lombok.SneakyThrows;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassDirPluginParserInfoHandler extends AbstractPluginParserInfoHandler{
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
        AntPathMatcher pathMatcher = new AntPathMatcher();
        List<URL> urls=new ArrayList<>();
        PluginDescribe pd=parseInfo.getPluginDescribe();

        final ClassDirPluginDescribe spd= ClassDirPluginDescribe.class.isAssignableFrom(pd.getClass())
                ?(ClassDirPluginDescribe) pd
                :parseInfo.decode(ClassDirPluginDescribe.class);

        Path baseDir= spd.getResource().getFile().toPath().resolve(spd.getRelativePath());
//        List<String> subPaths= CollectionUtils.isEmpty(spd.getSourceDirs())? Collections.singletonList("./"):spd.getSourceDirs();
//        subPaths.stream().forEach(sub->{
//            try {
//                Path workDir= spd.getResource().getFile().toPath().resolve(spd.getRelativePath());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
        return null;
    }
}
