package cc.catman.plugin.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DirResourceBrowser implements IResourceBrowser{
    private boolean visitorBeforeDir;
    private boolean visitorAfterDir;

    private List<String> skipDirs;

    public DirResourceBrowser() {
        this(false,false, new ArrayList<>());
    }

    public DirResourceBrowser(boolean visitorBeforeDir, boolean visitorAfterDir, List<String> skipDirs) {
        this.visitorBeforeDir = visitorBeforeDir;
        this.visitorAfterDir = visitorAfterDir;
        this.skipDirs = skipDirs;
    }

    public DirResourceBrowser(boolean visitorBeforeDir, boolean visitorAfterDir) {
        this(visitorBeforeDir,visitorAfterDir, Collections.emptyList());
    }


    @Override
    public boolean support(Resource resource) {
        if (!resource.isFile()){
            return false;
        }
        try {
            return resource.getFile().isDirectory();
        } catch (IOException e) {
            //  异常直接忽略?或者是记录一个日志
            // 这种无法访问肯定不正常
            log.warn("When obtaining the file type of resource {}, an unexpected error occurred. The error message is:"
            ,resource,e);
          return false;
        }
    }

    public DirResourceBrowser addSkip(String dir){
        this.skipDirs.add(dir);
        return this;
    }
    @Override
    public ResourceBrowserResult browser(Resource resource, ResourceVisitor visitor) {
        AtomicBoolean vc=new AtomicBoolean(true);
        try {
            Files.walkFileTree(resource.getFile().toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (skipDirs.contains(dir.getFileName().toString())){
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (visitorBeforeDir){
                        if (visitor.visitor(new FileSystemResource(dir))){
                            return FileVisitResult.CONTINUE;
                        }
                        vc.set(false);
                        return FileVisitResult.TERMINATE;
                    }
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (visitorBeforeDir){
                        if (visitor.visitor(new FileSystemResource(dir))){
                            return FileVisitResult.CONTINUE;
                        }
                        vc.set(false);
                        return FileVisitResult.TERMINATE;
                    }
                    return super.postVisitDirectory(dir, exc);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // 理论上这里只需要得到一个文件即可,找到符合要求的文件
                    // 目录
                    // 很遗憾这里只支持这两种停止状态
                    // 因为类似于jar之类的资源浏览器并不支持跳过子目录之类的操作,所以,如果有需求可以直接使用原始的Files.walkFileTree来
                    // 遍历普通文件
                    if (visitor.visitor(new FileSystemResource(file))){
                        return FileVisitResult.CONTINUE;
                    }
                    vc.set(false);
                    return FileVisitResult.TERMINATE;
                }
            });
            return ResourceBrowserResult.of(true,vc.get());
        } catch (IOException e) {
            log.warn("When obtaining the file type of resource {}, an unexpected error occurred. The error message is:"
                    ,resource,e);
            return ResourceBrowserResult.of(false,vc.get());
        }
    }
}
