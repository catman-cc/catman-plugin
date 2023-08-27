package cc.catman.plugin.provider;

import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.parser.IPluginParserContext;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于本地文件系统的插件提供者
 */
@Data
@Builder
public class LocalFileSystemPluginDescribeProvider implements IPluginDescribeProvider{
    public static final String[] DEFAULT_SUPPORT_PLUGIN_DESC_FILE_NAMES=new String[]{
            "**/cat-man-plugin.*"
    };
    /**
     * 文件系统的目录地址
     */
    private List<String> dirs;
    /**
     * 支持的插件描述文件名称
     */
    @Builder.Default
    private List<String> pluginDescFileNamesPatterns = Arrays.asList(DEFAULT_SUPPORT_PLUGIN_DESC_FILE_NAMES);

    protected IPluginParserContext pluginParserContext;

        @Override
     public List<StandardPluginDescribe> provider(){
       return   this.dirs.stream().map(Paths::get).map(basedir->{
             List<StandardPluginDescribe> standardPluginDescribes =new ArrayList<>();
           AntPathMatcher pathMatcher=new AntPathMatcher();
             try {

                 Files.walkFileTree(basedir, new SimpleFileVisitor<Path>() {
                     @Override
                     public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                         Path relativePath=basedir.relativize(dir);
                          return pluginDescFileNamesPatterns.stream().anyMatch(p-> pathMatcher.matchStart(p,relativePath.toString()))
                                 ?FileVisitResult.CONTINUE
                                 :FileVisitResult.SKIP_SUBTREE;
                     }
                     @Override
                     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                         // 理论上这里只需要得到一个文件即可,找到符合要求的文件
                         // 目录
                         Path relativePath=basedir.relativize(file);

                         if (pluginDescFileNamesPatterns.stream().anyMatch(p-> pathMatcher.match(p,relativePath.toString()))){
                             standardPluginDescribes.add(StandardPluginDescribe.builder().resource(new FileSystemResource(relativePath)).build());
                             // 如果在一个目录下找到了一个插件,那么就会跳过该文件的同级目录及其子目录,理论上,插件应该不会出现多级嵌套,所以这里这么处理应该没问题.
                             // 算了还是完全放开吧
                             return FileVisitResult.CONTINUE;
                         }
                         return FileVisitResult.CONTINUE;
                     }
                 });
             } catch (IOException e) {
                 throw new RuntimeException(e);
             }
             return standardPluginDescribes;
         }).flatMap(Collection::stream).collect(Collectors.toList());
    }

}
