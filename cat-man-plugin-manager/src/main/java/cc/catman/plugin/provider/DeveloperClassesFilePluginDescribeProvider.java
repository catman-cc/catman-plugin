package cc.catman.plugin.provider;

import cc.catman.plugin.describe.StandardPluginDescribe;
import cc.catman.plugin.describe.PluginParseInfo;
import cc.catman.plugin.describe.enmu.EPluginKind;
import cc.catman.plugin.describe.enmu.EPluginSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 为了开发模式下专门创建的一个类描述文件提供者
 * 其最特殊的地方就在于如果指定目录下有类描述文件,他会将其类型强制转换为: CLASSES_DIR
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperClassesFilePluginDescribeProvider extends AbstractPluginDescribeProvider {

    public static final String[] DEFAULT_SUPPORT_PLUGIN_DESC_FILE_NAMES = new String[]{
            "**/cat-man-plugin.*"
    };

    private String baseDir;
    /**
     * 文件系统的目录地址
     */
    private List<String> dirs;
    /**
     * 支持的插件描述文件名称
     */
    private List<String> pluginDescFileNamesPatterns = Arrays.asList(DEFAULT_SUPPORT_PLUGIN_DESC_FILE_NAMES);

    protected List<Consumer<StandardPluginDescribe>> afterParsers = createAfterParsers();

    protected List<Consumer<PluginParseInfo>> afterHandlers = createAfterHandlers();
    public static DeveloperClassesFilePluginDescribeProvider withRoot(String base,String ...dirs){
        return new DeveloperClassesFilePluginDescribeProvider(base,dirs);
    }
    public static DeveloperClassesFilePluginDescribeProvider of(String... dirs){
        return new DeveloperClassesFilePluginDescribeProvider(Arrays.asList(dirs));
    }

    public DeveloperClassesFilePluginDescribeProvider(List<String> dirs) {
        this(System.getProperty("user.dir"), dirs);
    }

    public DeveloperClassesFilePluginDescribeProvider(String baseDir, String... dirs) {
        this(baseDir, Arrays.asList(dirs));
    }

    public DeveloperClassesFilePluginDescribeProvider(String baseDir, List<String> dirs) {
        this.baseDir = baseDir;
        this.dirs = dirs;
    }

    private List<Consumer<StandardPluginDescribe>> createAfterParsers() {
        return Collections.singletonList(pd -> {
            // 强制重写插件类型
            // 原因在于:
            // cat-man-plugin-annotation-processor项目生成的描述文件可能不适用开发模式
            pd.setKind(EPluginKind.CLASSES_DIR.name());
            pd.setSource(EPluginSource.LOCAL.name());
        });
    }


    private List<Consumer<PluginParseInfo>> createAfterHandlers() {
        return new ArrayList<>();
    }

    @Override
    public List<StandardPluginDescribe> provider() {
        Path workDir = Paths.get(baseDir);
        // 扫描工作目录下的所有地址,为其创建插件实例.
        return this.dirs.stream()
                .map(workDir::resolve)
                .map(basedir -> {
                    List<StandardPluginDescribe> standardPluginDescribes = new ArrayList<>();
                    AntPathMatcher pathMatcher = new AntPathMatcher();
                    try {
                        Files.walkFileTree(basedir, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                                Path relativePath = basedir.relativize(dir);
                                return pluginDescFileNamesPatterns.stream().anyMatch(p -> pathMatcher.matchStart(p, relativePath.toString()))
                                        ? FileVisitResult.CONTINUE
                                        : FileVisitResult.SKIP_SUBTREE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                                // 理论上这里只需要得到一个文件即可,找到符合要求的文件
                                Path relativePath = basedir.relativize(file);

                                if (pluginDescFileNamesPatterns.stream().anyMatch(p -> pathMatcher.match(p, relativePath.toString()))) {
                                    standardPluginDescribes.add(
                                            StandardPluginDescribe.builder().describeResource(new FileSystemResource(file))
                                                    .afterParsers(afterParsers)
                                                    .afterHandlers(afterHandlers)
                                                    .build()

                                    );
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

    public DeveloperClassesFilePluginDescribeProvider addAfterParsers(Consumer<StandardPluginDescribe>... consumer) {
        this.afterParsers.addAll(Arrays.asList(consumer));
        return this;
    }

    public DeveloperClassesFilePluginDescribeProvider addAfterHandler(Consumer<PluginParseInfo>... consumer) {
        this.afterHandlers.addAll(Arrays.asList(consumer));
        return this;
    }

}
